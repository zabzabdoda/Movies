package com.zabzabdoda.config;

import com.zabzabdoda.controllers.AccessDeniedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().and()
                .authorizeRequests()
                .antMatchers("/assets/js/**", "/assets/css/**").permitAll()
                .mvcMatchers("/public/**").permitAll()
                .mvcMatchers("/private/**").authenticated()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .and().formLogin().usernameParameter("email").loginPage("/public/login")
                .and().formLogin().defaultSuccessUrl("/private/success").failureUrl("/public/login?error=true").permitAll()
                .and().logout().logoutSuccessUrl("/public/login?logout=true").invalidateHttpSession(true).permitAll()
                .and().exceptionHandling().accessDeniedHandler(new AccessDeniedController())
                .and().httpBasic();

        return http.build();
    }


}
