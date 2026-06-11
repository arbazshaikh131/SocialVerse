package com.example.socialmedia.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "postId is required")
    private Long postId;

    @NotBlank(message = "Text is required")
    private String text;
}
