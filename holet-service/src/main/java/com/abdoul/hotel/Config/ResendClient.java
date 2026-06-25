package com.abdoul.hotel.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class ResendClient {
    @Value("${resend.access}")
    private String resendKey;

    @Bean
    public WebClient resendClient (){
        return WebClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + resendKey)
                .defaultHeader("User-Agent", "Gentel/1.0")
                .build();
    }
}
