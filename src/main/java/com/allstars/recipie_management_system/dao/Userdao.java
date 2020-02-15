package com.allstars.recipie_management_system.dao;


import com.allstars.recipie_management_system.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


@Repository
public interface Userdao extends CrudRepository<User, String> {
    User findByEmailId(String emailId);

    @Query("SELECT count(emailId) FROM User WHERE emailId=:emailId")
    int isEmailPresent(@Param("emailId") String emailId);
}
