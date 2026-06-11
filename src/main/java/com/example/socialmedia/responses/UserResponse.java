package com.example.socialmedia.responses;

import com.example.socialmedia.entities.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String bio;
    private String profileImageUrl;
    private int followerCount;
    private int followingCount;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.bio = user.getBio();
        this.profileImageUrl = user.getProfileImageUrl();
        this.followerCount = user.getFollowers().size();
        this.followingCount = user.getFollowing().size();
    }
}
