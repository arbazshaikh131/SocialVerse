package com.example.socialmedia.responses;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String message;
    private Long userId;
    private String username;
    private String accessToken;
    private String refreshToken;
}
