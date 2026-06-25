package com.abdoul.hotel.Utils;

import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TwilioUtil {
    @Value("${twilio.trial}")
    private String trial;

    @Async
    public void sendWelcomeSmsWithPhoneVerification (String receiver, String name, String code) {
        try{
            Message newMessage = Message.creator(new PhoneNumber(receiver),
                    new PhoneNumber(trial),
                    "Welcome to Gentel, " + name + ". Your phone number verification code is " + code).create();

            log.info("Sms successfully sent with sid {}", newMessage.getSid());
        }

        catch (TwilioException ex){
            log.error("Sms failed {}", ex.getMessage(), ex);
        }
    }
}