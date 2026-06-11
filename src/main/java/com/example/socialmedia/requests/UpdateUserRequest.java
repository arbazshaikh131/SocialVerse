package com.example.socialmedia.requests;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String bio;
    private String profileImageUrl;
}
