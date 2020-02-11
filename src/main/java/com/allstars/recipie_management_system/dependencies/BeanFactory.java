package com.allstars.recipie_management_system.dependencies;

import com.allstars.recipie_management_system.validators.RecipieValidator;
import com.allstars.recipie_management_system.validators.UserValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class BeanFactory {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserValidator userValidator(){
        return new UserValidator();
    }

    @Bean
    public RecipieValidator recipieValidator(){
        return new RecipieValidator();
    }

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint(){
        return new BasicAuthenticationEntryPoint() {
            @Override
            public void afterPropertiesSet() throws Exception {
                setRealmName("recipie_management_system");
                super.afterPropertiesSet();
            }

            @Override
            public void commence(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthenticationException authException) throws IOException {
                response.addHeader("WWW-Authenticate", "Basic realm = "+getRealmName());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("");
            }
        };
    }
}
