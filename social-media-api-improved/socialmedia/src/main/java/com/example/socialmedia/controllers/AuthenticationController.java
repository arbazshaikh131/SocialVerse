package com.example.socialmedia.controllers;

import com.example.socialmedia.entities.RefreshToken;
import com.example.socialmedia.entities.User;
import com.example.socialmedia.exceptions.BadRequestException;
import com.example.socialmedia.exceptions.UnauthorizedException;
import com.example.socialmedia.requests.RefreshTokenRequest;
import com.example.socialmedia.requests.UserRequest;
import com.example.socialmedia.responses.AuthenticationResponse;
import com.example.socialmedia.security.JwtTokenProvider;
import com.example.socialmedia.services.RefreshTokenService;
import com.example.socialmedia.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login and token refresh")
public class AuthenticationController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserRequest request) {
        if (userService.findByUsername(request.getUsername()) != null) {
            throw new BadRequestException("Username is already taken");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.createUser(user);

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage("User registered successfully");
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setAccessToken("Bearer " + jwtTokenProvider.generateToken(auth));
        response.setRefreshToken(refreshTokenService.createRefreshToken(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT tokens")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody UserRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userService.findByUsername(request.getUsername());

        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage("Login successful");
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setAccessToken("Bearer " + jwtTokenProvider.generateToken(auth));
        response.setRefreshToken(refreshTokenService.createRefreshToken(user));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.getByUserId(request.getUserId());
        if (!token.getToken().equals(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        if (refreshTokenService.isExpired(token)) {
            throw new UnauthorizedException("Refresh token has expired, please log in again");
        }
        String newAccessToken = jwtTokenProvider.generateTokenByUserId(token.getUser().getId());

        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage("Token refreshed successfully");
        response.setUserId(token.getUser().getId());
        response.setAccessToken("Bearer " + newAccessToken);
        return ResponseEntity.ok(response);
    }
}
