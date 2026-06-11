package com.example.socialmedia.controllers;

import com.example.socialmedia.requests.CreateCommentRequest;
import com.example.socialmedia.requests.UpdateCommentRequest;
import com.example.socialmedia.responses.CommentResponse;
import com.example.socialmedia.services.CommentService;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment on posts")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Get comments (paginated, optionally filtered by userId and/or postId)")
    public Page<CommentResponse> getAllComments(
            @RequestParam Optional<Long> userId,
            @RequestParam Optional<Long> postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return commentService.getAllComments(userId, postId, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get a comment by ID")
    public CommentResponse getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PostMapping
    @Operation(summary = "Create a comment on a post")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(request));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment")
    public CommentResponse updateComment(@PathVariable Long commentId,
                                         @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
