package com.example.socialmedia.controllers;

import com.example.socialmedia.requests.CreateLikeRequest;
import com.example.socialmedia.responses.LikeResponse;
import com.example.socialmedia.services.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "Like and unlike posts")
@SecurityRequirement(name = "bearerAuth")
public class LikeController {

    private final LikeService likeService;

    @GetMapping
    @Operation(summary = "Get likes (paginated, optionally filtered by userId and/or postId)")
    public Page<LikeResponse> getAllLikes(
            @RequestParam Optional<Long> userId,
            @RequestParam Optional<Long> postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return likeService.getAllLikes(userId, postId, PageRequest.of(page, size));
    }

    @GetMapping("/{likeId}")
    @Operation(summary = "Get a like by ID")
    public LikeResponse getLikeById(@PathVariable Long likeId) {
        return likeService.getLikeById(likeId);
    }

    @PostMapping
    @Operation(summary = "Like a post")
    public ResponseEntity<LikeResponse> createLike(@Valid @RequestBody CreateLikeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeService.createLike(request));
    }

    @DeleteMapping("/{likeId}")
    @Operation(summary = "Remove a like")
    public ResponseEntity<Void> deleteLike(@PathVariable Long likeId) {
        likeService.deleteLike(likeId);
        return ResponseEntity.noContent().build();
    }
}
