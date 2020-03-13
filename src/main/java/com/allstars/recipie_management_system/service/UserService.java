package com.allstars.recipie_management_system.service;


import com.allstars.recipie_management_system.dao.Userdao;
import com.allstars.recipie_management_system.entity.User;
import com.allstars.recipie_management_system.entity.UserDetailsCustom;
import com.allstars.recipie_management_system.errors.RegistrationStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private Userdao userDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    MeterRegistry registry;

    Timer userTimer;

    public User saveUser(User user){
        userTimer = registry.timer("custom.metrics.timer", "Backend", "UserSAVE");
        try {
            passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userTimer = registry.timer("custom.metrics.timer", "Backend", "UserSAVE");
            final User[] users = new User[1];

            userTimer.record(()-> users[0] = userDao.save(user));
            return users[0];

//            userTimer.record(()-> {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(1500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            userTimer.record(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            return null;
        }
        //return  user;
    }

//    public User saveUser(User users) {
//        userTimer = registry.timer("custom.metrics.timer", "Backend", "UserSAVE");
//        final User[] user = new User[1];
//        userTimer.record(()-> user[0] = userDao.save(users));
//        return user[0];
//    }


    public Boolean isEmailPresent(String emailId) {
        return userDao.isEmailPresent(emailId) > 0 ? true : false;
    }

    public User findByEmailId(String emailId){
        return userDao.findByEmailId(emailId);
    }

    public User getUser(String emailId) {
        userTimer = registry.timer("custom.metrics.timer", "Backend", "UserGET");
        final User[] user = new User[1];
        userTimer.record(()-> user[0] = userDao.findByEmailId(emailId));
        return user[0] ;
    }

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        User user = userDao.findByEmailId(emailId);
        if(user==null) throw new UsernameNotFoundException("User with given emailId does not exist");
        else return new UserDetailsCustom(user);
    }

    public RegistrationStatus getRegistrationStatus(BindingResult errors) {
        FieldError emailIdError = errors.getFieldError("emailId");
        FieldError passwordError = errors.getFieldError("password");
        String emailIdErrorMessage = emailIdError == null ? "-" : emailIdError.getCode();
        String passwordErrorMessage = passwordError == null ? "-" : passwordError.getCode();
        RegistrationStatus registrationStatus = new RegistrationStatus(emailIdErrorMessage, passwordErrorMessage);
        return registrationStatus;
    }

    public Boolean updateUserInfo(User newUser, String emailId, String Password){
        if (newUser.getEmailId()!=null || newUser.getAccount_created()!=null || newUser.getAccount_updated()!=null || newUser.getUuid()!=null){
            return false;
        }
        else{
            User currUser = userDao.findByEmailId(emailId);
            if(currUser.getEmailId().equals(emailId)) {

                    PasswordValidator validator = new PasswordValidator(Arrays.asList(
                            new LengthRule(9, 30),
                            new CharacterRule(EnglishCharacterData.UpperCase, 1),
                            new CharacterRule(EnglishCharacterData.LowerCase, 1),
                            new CharacterRule(EnglishCharacterData.Digit, 1),
                            new CharacterRule(EnglishCharacterData.Special, 1),
                            new WhitespaceRule()));
                    RuleResult result = validator.validate(new PasswordData(newUser.getPassword()));
                    if(result.isValid()) {
                        currUser.setFirst_name(newUser.getFirst_name());
                        currUser.setLast_name(newUser.getLast_name());
                        currUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
                        currUser.setAccount_updated(new Date());
                        userDao.save(currUser);
                        return true;
                    }
                    else{
                        return false;
                    }

            }
            else{
                return false;
            }
        }

    }

}
