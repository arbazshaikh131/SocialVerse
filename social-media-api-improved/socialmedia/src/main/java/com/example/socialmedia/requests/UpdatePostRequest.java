package com.example.socialmedia.requests;

import lombok.Data;

@Data
public class UpdatePostRequest {
    private String title;
    private String text;
}
