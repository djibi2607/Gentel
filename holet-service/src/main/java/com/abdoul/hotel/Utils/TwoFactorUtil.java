package com.abdoul.hotel.Utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TwoFactorUtil {

    public String createCode (){
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(100000, 1000000);

        return String.valueOf(code);
    }
}
