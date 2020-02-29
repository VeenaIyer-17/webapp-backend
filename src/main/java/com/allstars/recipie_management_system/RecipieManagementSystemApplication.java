package com.allstars.recipie_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages="com.allstars.recipie_management_system.dao")
@EnableCaching
public class RecipieManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipieManagementSystemApplication.class, args);
    }

}
