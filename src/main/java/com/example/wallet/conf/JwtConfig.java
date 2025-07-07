package com.example.wallet.conf;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtConfig {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("${security.jwt.expiration}")
    private long expiration; // in milliseconds

    private final AppStartupTime appStartupTime;

    public JwtConfig(AppStartupTime appStartupTime) {
        this.appStartupTime = appStartupTime;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            Date issuedAt = claims.getIssuedAt();
            if (issuedAt == null || issuedAt.toInstant().isBefore(appStartupTime.getStartedAt())) {
                return false; // token was issued before app restart
            }

            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
