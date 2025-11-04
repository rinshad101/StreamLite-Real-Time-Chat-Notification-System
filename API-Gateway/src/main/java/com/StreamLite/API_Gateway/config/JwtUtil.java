package com.StreamLite.API_Gateway.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
class JwtUtil {


    @Value("${JWT_SECRET_KEY}")
    private String SECRET_KEY;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }


    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractTokenFromCookie(List<String> cookies) {
        if (cookies == null) return null;
        return cookies.stream()
                .flatMap(c -> List.of(c.split(";")).stream())
                .map(String::trim)
                .filter(c -> c.startsWith("jwt="))
                .map(c -> c.substring(4))
                .findFirst()
                .orElse(null);
    }
}