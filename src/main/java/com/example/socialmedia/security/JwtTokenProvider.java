package com.example.socialmedia.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expires-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getId());
    }

    public String generateTokenByUserId(Long userId) {
        return buildToken(userId);
    }

    private String buildToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
            .setSubject(Long.toString(userId))
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty");
        }
        return false;
    }
}
