package com.example.socialmedia.responses;

import com.example.socialmedia.entities.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String text;
    private LocalDateTime createdAt;
    private int likeCount;
    private List<LikeResponse> likes;

    public PostResponse(Post post, List<LikeResponse> likes) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.username = post.getUser().getUsername();
        this.title = post.getTitle();
        this.text = post.getText();
        this.createdAt = post.getCreatedAt();
        this.likes = likes;
        this.likeCount = likes.size();
    }
}
