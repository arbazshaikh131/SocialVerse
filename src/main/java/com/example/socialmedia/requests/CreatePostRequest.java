package com.example.socialmedia.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Text is required")
    private String text;
}
