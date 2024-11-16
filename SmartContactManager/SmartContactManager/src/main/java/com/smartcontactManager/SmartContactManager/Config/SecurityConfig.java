package com.smartcontactManager.SmartContactManager.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    //create user and login using java code with in memory service
    @Bean
    public UserDetailsService getUserDetailsService(){
        return new UserDetailServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
        //User detail service the object lana hai
        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
        //Password encoder the object lana hai
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(authorize->{
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        httpSecurity.csrf(csrf -> csrf
                .ignoringRequestMatchers("/logout") // If you want to disable CSRF for logout, but not recommended
        );
        httpSecurity.formLogin(form-> {

                    form.loginPage("/login");
                    form.loginProcessingUrl("/authentication");
                    form.successForwardUrl("/user/dashboard,true");
                    form.failureForwardUrl("/login?error=true");
                    form.permitAll();
                    form.usernameParameter("email");
                    form.passwordParameter("password");


                    form.successHandler((request, response, authentication) -> {
                        // Custom success handling
                        response.sendRedirect("/user/dashboard");
                    })
                    .failureHandler((request, response, authenticationException) -> {
                        // Custom failure handling
                        response.sendRedirect("/login?error=true");
                    })
                     .permitAll();

        })

                .logout(logout -> {
                    logout
                            .logoutUrl("/logout") // Default logout URL
                            .logoutSuccessUrl("/login?logout=true")// Redirect after successful logout
                            .invalidateHttpSession(true)// Invalidate session on logout
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")// Optionally delete cookies
                            .permitAll();
                });

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
