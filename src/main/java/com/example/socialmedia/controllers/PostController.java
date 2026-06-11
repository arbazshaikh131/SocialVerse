package com.example.socialmedia.controllers;

import com.example.socialmedia.requests.CreatePostRequest;
import com.example.socialmedia.requests.UpdatePostRequest;
import com.example.socialmedia.responses.PostResponse;
import com.example.socialmedia.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Create, read, update and delete posts")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Get all posts (paginated, optionally filtered by userId)")
    public Page<PostResponse> getAllPosts(
            @RequestParam Optional<Long> userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return postService.getAllPosts(userId, PageRequest.of(page, size, sort));
    }

    @GetMapping("/search")
    @Operation(summary = "Search posts by keyword in title or text")
    public Page<PostResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.searchPosts(keyword, PageRequest.of(page, size));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get a single post by ID")
    public PostResponse getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    @Operation(summary = "Create a new post")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update a post")
    public PostResponse updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequest request) {
        return postService.updatePost(postId, request);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete a post")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
