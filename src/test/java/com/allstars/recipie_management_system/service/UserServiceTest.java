package com.allstars.recipie_management_system.service;

import com.allstars.recipie_management_system.dao.Userdao;
import com.allstars.recipie_management_system.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private Userdao userdao;

    private static User user;

    @Before
    public void setUp() {
        this.user = new User("stringID1234","ravi","kiran","kiranhun@gmail.com","WonderFul@28",new Date(),new Date());
    }

    @Test
    public void userRegisterTest(){
        userService.saveUser(user);
        Mockito.verify(userdao).save(user);
    }
}
