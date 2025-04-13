
package com.sms.SchoolManagementBrahmada.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sms.SchoolManagementBrahmada.models.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time-ms}")
    private long expirationTimeMs;

    @Value("${security.jwt.issuer}")
    private String issuer;

    public String createJwtToken(AppUser user) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(user.getEmail())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .claim("role", user.getRole()) 
                .signWith(key)
                .compact();
    }

  
    public Claims getTokenClaims(String token) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expDate = claims.getExpiration();
            Date currentDate = new Date();

            if (currentDate.before(expDate)) {
                return claims;
            }

        } catch (Exception ex) {
            System.out.println("JWT parsing/validation failed: " + ex.getMessage());
        }

        return null;
    }
}



