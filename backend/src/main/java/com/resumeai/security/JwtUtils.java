package com.resumeai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String DEFAULT_SECRET = "ThisIsAStrongSecretKeyForResumeScreener2026ThisIsAStrongSecretKeyForResumeScreener2026";
    private static final long DEFAULT_EXPIRATION_MS = 86400000L;

    @Value("${app.jwt.secret:}")
    private String secret;

    @Value("${app.jwt.expiration-ms:}")
    private String expirationMsStr;

    private SecretKey key;

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(now)
            .expiration(new Date(now.getTime() + getExpirationMs()))
            .signWith(getKey())
            .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && getClaims(token).getExpiration().after(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getKey() {
        if (key == null) {
            String secretKey = (secret != null && !secret.isBlank()) ? secret : DEFAULT_SECRET;
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    private long getExpirationMs() {
        if (expirationMsStr != null && !expirationMsStr.isBlank()) {
            try {
                return Long.parseLong(expirationMsStr);
            } catch (NumberFormatException e) {
                return DEFAULT_EXPIRATION_MS;
            }
        }
        return DEFAULT_EXPIRATION_MS;
    }
}
