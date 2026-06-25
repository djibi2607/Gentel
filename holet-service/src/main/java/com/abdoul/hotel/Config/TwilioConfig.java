package com.abdoul.hotel.Config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TwilioConfig {
    @Value("${twilio.sid}")
    private String twilioSid;

    @Value("${twilio.token}")
    private String twilioToken;

    @PostConstruct
    public void twilio (){
       Twilio.init(twilioSid, twilioToken);
    }
}
