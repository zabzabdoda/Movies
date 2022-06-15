package com.zabzabdoda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.zabzabdoda.repository")
@EntityScan(value = "com.zabzabdoda.model")
@EnableJpaAuditing(auditorAwareRef = "AuditAwareImpl")
@EnableConfigurationProperties(ConfigProperties.class)
public class MoviesApplication {

    public static void main(String[] args){
        SpringApplication.run(MoviesApplication.class,args);
    }

}
