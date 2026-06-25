package com.abdoul.hotel.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class ResendUtil {
    private final WebClient resendClient;

    public ResendUtil (WebClient resendClient){
        this.resendClient = resendClient;
    }

    @Value("${my.email}")
    private String myEmail;

    public void sendWelcomeEmailWithEmailVerification(String name, String code){

        Map<String,String> body = new LinkedHashMap<>();
        body.put("from", "Acme <onboarding@resend.dev>");
        body.put("to", myEmail);
        body.put("subject", "Account creation");
        body.put("html",  "<p style=\"text-align: center;\"><strong>Account Creation</strong></p>" +
                "<p>Welcome to Gentel, " + name + ". Your verification code is " + code + ". Your account has successfully been created. Please proceed to login and upload the required documents, otherwise your account will be deactivated in 14 days.</p>" +
                "<p>&nbsp;</p>" +
                "<p>Thank you,<br>The Gentel Team.</p>");

        resendClient.post()
                .uri("/emails")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3L)
                .doOnError(error -> log.error("Email failed {}", error.getMessage(), error))
                .subscribe();
    }
}
