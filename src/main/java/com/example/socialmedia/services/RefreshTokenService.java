package com.example.socialmedia.services;

import com.example.socialmedia.entities.RefreshToken;
import com.example.socialmedia.entities.User;
import com.example.socialmedia.exceptions.ResourceNotFoundException;
import com.example.socialmedia.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-token-expires-ms}")
    private long refreshTokenExpiresMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(User user) {
        RefreshToken token = refreshTokenRepository.findByUserId(user.getId())
            .orElse(new RefreshToken());
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiresMs / 1000));
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    public RefreshToken getByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found for user: " + userId));
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
