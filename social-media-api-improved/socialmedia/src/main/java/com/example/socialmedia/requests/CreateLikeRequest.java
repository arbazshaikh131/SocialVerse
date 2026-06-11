package com.example.socialmedia.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLikeRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "postId is required")
    private Long postId;
}
