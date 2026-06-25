package com.warnabis.hotel_springboot_application.security;

import com.warnabis.hotel_springboot_application.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(Integer userId, String email, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getExpirationMs());

        return Jwts.builder()
          .subject(String.valueOf(userId))
          .claim("email", email)
          .claim("role", role)
          .issuedAt(Date.from(now))
          .expiration(Date.from(expiry))
          .signWith(getSigningKey())
          .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}