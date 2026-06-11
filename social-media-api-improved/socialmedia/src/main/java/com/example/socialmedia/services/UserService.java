package com.example.socialmedia.services;

import com.example.socialmedia.entities.User;
import com.example.socialmedia.exceptions.BadRequestException;
import com.example.socialmedia.exceptions.ResourceNotFoundException;
import com.example.socialmedia.repository.CommentRepository;
import com.example.socialmedia.repository.LikeRepository;
import com.example.socialmedia.repository.PostRepository;
import com.example.socialmedia.repository.UserRepository;
import com.example.socialmedia.requests.UpdateUserRequest;
import com.example.socialmedia.responses.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::new);
    }

    public Page<UserResponse> searchUsers(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable)
            .map(UserResponse::new);
    }

    public UserResponse getUserById(Long userId) {
        User user = findUserOrThrow(userId);
        return new UserResponse(user);
    }

    public User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUserProfile(Long userId, UpdateUserRequest request) {
        User user = findUserOrThrow(userId);
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());
        return new UserResponse(userRepository.save(user));
    }

    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        User follower = findUserOrThrow(followerId);
        User following = findUserOrThrow(followingId);
        if (follower.getFollowing().contains(following)) {
            throw new BadRequestException("You are already following this user");
        }
        follower.getFollowing().add(following);
        userRepository.save(follower);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        User follower = findUserOrThrow(followerId);
        User following = findUserOrThrow(followingId);
        if (!follower.getFollowing().contains(following)) {
            throw new BadRequestException("You are not following this user");
        }
        follower.getFollowing().remove(following);
        userRepository.save(follower);
    }

    public Page<UserResponse> getFollowers(Long userId, Pageable pageable) {
        findUserOrThrow(userId);
        return userRepository.findAll(pageable)
            .map(UserResponse::new); // simplified — full impl would use a dedicated query
    }

    public List<Object> getUserActivity(Long userId) {
        List<Long> postIds = postRepository.findTopPostIdsByUserId(userId);
        if (postIds.isEmpty()) return List.of();
        List<Object> results = new ArrayList<>();
        results.addAll(commentRepository.findUserCommentsByPostIds(postIds));
        results.addAll(likeRepository.findUserLikesByPostIds(postIds));
        return results;
    }
}
