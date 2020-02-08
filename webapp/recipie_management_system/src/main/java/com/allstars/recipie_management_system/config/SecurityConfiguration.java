package com.allstars.recipie_management_system.config;
import com.allstars.recipie_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

   @Autowired
   private UserService userService;

   @Autowired
   private AuthenticationEntryPoint basicAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        try {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/v1/user").permitAll()
                    .antMatchers( HttpMethod.GET,"/v1/recipies").permitAll()
                    .antMatchers( HttpMethod.GET,"/v1/recipie/{id}").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

           /* http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "v1/user/self").fullyAuthenticated()
                    .anyRequest().permitAll();
            http.authorizeRequests()
                    .antMatchers(HttpMethod.PUT, "v1/user/self").fullyAuthenticated()
                    .anyRequest().permitAll();
            http.authorizeRequests()
                    .antMatchers(HttpMethod.POST, "v1/recipie/").fullyAuthenticated()
                    .anyRequest().permitAll();
            http.authorizeRequests()
                    .antMatchers(HttpMethod.DELETE, "/v1/recipie/{id}").fullyAuthenticated()
                    .anyRequest().permitAll();*/

            /*http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "v1/user").permitAll()
                    .antMatchers(HttpMethod.GET, "v1/recipie/{id}").permitAll()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

            http.httpBasic().authenticationEntryPoint(basicAuthenticationEntryPoint).and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
*/
            //http.authorizeRequests().antMatchers(HttpMethod.POST, "v1/user").permitAll();
            //http.authorizeRequests().antMatchers(HttpMethod.GET, "v1/recipie/{id}").permitAll();

        } catch(Exception exc) {

        }
    }
}
