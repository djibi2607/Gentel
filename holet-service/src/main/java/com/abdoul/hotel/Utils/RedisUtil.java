package com.abdoul.hotel.Utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    public RedisUtil (RedisTemplate<String, String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void saveCode (String code, String userId, String prefix){
        redisTemplate.opsForValue().set(prefix + userId, code, Duration.ofMinutes(15));
    }

    public String getCode (String userId, String prefix){
        return redisTemplate.opsForValue().get(prefix + userId);
    }

    public void saveToken (String prefix, String token, String userId, Duration duration){
        redisTemplate.opsForValue().set(prefix + token, userId, duration);
    }

    public String getIdFromToken (String prefix, String token){
        return redisTemplate.opsForValue().get(prefix + token);
    }
}
