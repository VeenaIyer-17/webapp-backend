package com.allstars.recipie_management_system.controller;


import com.allstars.recipie_management_system.entity.User;
import com.allstars.recipie_management_system.errors.RegistrationStatus;
import com.allstars.recipie_management_system.service.UserService;
import com.allstars.recipie_management_system.validators.UserValidator;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    MeterRegistry registry;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }


    @RequestMapping(value = "v1/user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult errors,
                                        HttpServletResponse response) throws Exception {
        registry.counter("custom.metrics.counter", "ApiCall", "UserPost").increment();
        log.info("Inside post /user mapping");
        RegistrationStatus registrationStatus;

        if(errors.hasErrors()) {
            registrationStatus = userService.getRegistrationStatus(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    registrationStatus);
        }else {
            user.setAccount_created(new Date());
            user.setAccount_updated(new Date());
            registrationStatus = new RegistrationStatus();
            User u = userService.saveUser(user);

            return  new ResponseEntity<User>(u, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value="v1/user/self" ,method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@RequestHeader("Authorization") String token, HttpServletRequest request) throws UnsupportedEncodingException {
        registry.counter("custom.metrics.counter", "ApiCall", "UserGet").increment();
        log.info("Inside get /self mapping");

       try {
           if(token == null){
               return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
           }
           String userDetails[] = decryptAuthenticationToken(token);

           if (!(userService.isEmailPresent(userDetails[0])))
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
           else
               return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userDetails[0]));
       }catch(Exception e){
           return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
       }
    }

    @RequestMapping(value = "v1/user/self", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String header, @Valid @RequestBody User user, BindingResult errors,
                                             HttpServletResponse response) throws UnsupportedEncodingException {
        registry.counter("custom.metrics.counter", "ApiCall", "UserPut").increment();
        log.info("Inside put /self mapping");
        try {
            if(header == null){
                return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            String[] userDetails = decryptAuthenticationToken(header);

            if (userService.updateUserInfo(user, userDetails[0], userDetails[1])) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
            }
        } catch(Exception e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    public String[] decryptAuthenticationToken(String token) throws Exception {
        try {
            String[] basicAuthToken = token.split(" ");
            byte[] authKeys = Base64.getDecoder().decode(basicAuthToken[1]);
            return new String(authKeys, "utf-8").split(":");
        } catch(Exception e) {
            throw new Exception("Unauthorized");
        }
    }
}
