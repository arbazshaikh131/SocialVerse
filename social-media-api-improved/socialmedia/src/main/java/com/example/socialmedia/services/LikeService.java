package com.example.socialmedia.services;

import com.example.socialmedia.entities.Like;
import com.example.socialmedia.entities.Post;
import com.example.socialmedia.entities.User;
import com.example.socialmedia.exceptions.BadRequestException;
import com.example.socialmedia.exceptions.ResourceNotFoundException;
import com.example.socialmedia.repository.LikeRepository;
import com.example.socialmedia.requests.CreateLikeRequest;
import com.example.socialmedia.responses.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostService postService;

    public Page<LikeResponse> getAllLikes(Optional<Long> userId, Optional<Long> postId, Pageable pageable) {
        Page<Like> likes;
        if (userId.isPresent() && postId.isPresent()) {
            likes = likeRepository.findByUserIdAndPostId(userId.get(), postId.get(), pageable);
        } else if (userId.isPresent()) {
            likes = likeRepository.findByUserId(userId.get(), pageable);
        } else if (postId.isPresent()) {
            likes = likeRepository.findByPostId(postId.get(), pageable);
        } else {
            likes = likeRepository.findAll(pageable);
        }
        return likes.map(LikeResponse::new);
    }

    public LikeResponse getLikeById(Long likeId) {
        Like like = likeRepository.findById(likeId)
            .orElseThrow(() -> new ResourceNotFoundException("Like", likeId));
        return new LikeResponse(like);
    }

    public LikeResponse createLike(CreateLikeRequest request) {
        // Prevent duplicate likes
        likeRepository.findByUserIdAndPostId(request.getUserId(), request.getPostId()).ifPresent(l -> {
            throw new BadRequestException("User has already liked this post");
        });
        User user = userService.findUserOrThrow(request.getUserId());
        Post post = postService.getRawPostOrThrow(request.getPostId());
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        return new LikeResponse(likeRepository.save(like));
    }

    public void deleteLike(Long likeId) {
        if (!likeRepository.existsById(likeId)) {
            throw new ResourceNotFoundException("Like", likeId);
        }
        likeRepository.deleteById(likeId);
    }
}
