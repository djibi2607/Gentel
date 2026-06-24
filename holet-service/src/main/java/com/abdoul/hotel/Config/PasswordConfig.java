package com.abdoul.hotel.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;

@Configuration
public class PasswordConfig{
    @Bean
    public Argon2Password4jPasswordEncoder encoder(){ return new Argon2Password4jPasswordEncoder(); }
}
