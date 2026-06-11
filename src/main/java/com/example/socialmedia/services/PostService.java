package com.example.socialmedia.services;

import com.example.socialmedia.entities.Post;
import com.example.socialmedia.entities.User;
import com.example.socialmedia.exceptions.ResourceNotFoundException;
import com.example.socialmedia.repository.LikeRepository;
import com.example.socialmedia.repository.PostRepository;
import com.example.socialmedia.requests.CreatePostRequest;
import com.example.socialmedia.requests.UpdatePostRequest;
import com.example.socialmedia.responses.LikeResponse;
import com.example.socialmedia.responses.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository,
                       UserService userService,
                       LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.likeRepository = likeRepository;
    }

    public Page<PostResponse> getAllPosts(Optional<Long> userId, Pageable pageable) {
        Page<Post> posts = userId.isPresent()
            ? postRepository.findByUserId(userId.get(), pageable)
            : postRepository.findAll(pageable);
        return posts.map(this::toResponse);
    }

    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository
            .findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(keyword, keyword, pageable)
            .map(this::toResponse);
    }

    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        return toResponse(post);
    }

    public Post getRawPostOrThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
    }

    public PostResponse createPost(CreatePostRequest request) {
        User user = userService.findUserOrThrow(request.getUserId());
        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        return toResponse(postRepository.save(post));
    }

    public PostResponse updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getText() != null) post.setText(request.getText());
        return toResponse(postRepository.save(post));
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post", postId);
        }
        postRepository.deleteById(postId);
    }

    private PostResponse toResponse(Post post) {
        List<LikeResponse> likes = likeRepository.findByPostId(post.getId(), Pageable.unpaged())
            .stream().map(LikeResponse::new).collect(Collectors.toList());
        return new PostResponse(post, likes);
    }
}
