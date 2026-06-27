package com.abdoul.hotel.Utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${secret.key}")
    private String secretKey;

    public String generateAccessToken (String userId){
        return Jwts.builder().signWith(Keys.hmacShaKeyFor(secretKey.getBytes())).subject(userId).expiration(new Date(System.currentTimeMillis() + 60 * 20 * 1000)).issuedAt(new Date()).compact();
    }

    public String extractIdFromToken (String token){
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isTokenValid (String token){
        try{
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseSignedClaims(token);

            return true;
        }
        catch (JwtException ex){
            log.error("Jwt parsing failed {}", ex.getMessage(), ex);
            return false;
        }
    }
}
