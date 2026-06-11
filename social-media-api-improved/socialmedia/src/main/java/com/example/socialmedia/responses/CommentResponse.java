package com.example.socialmedia.responses;

import com.example.socialmedia.entities.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private Long userId;
    private String username;
    private String text;
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.username = comment.getUser().getUsername();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
    }
}
