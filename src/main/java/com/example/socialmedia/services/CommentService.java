package com.example.socialmedia.services;

import com.example.socialmedia.entities.Comment;
import com.example.socialmedia.entities.Post;
import com.example.socialmedia.entities.User;
import com.example.socialmedia.exceptions.ResourceNotFoundException;
import com.example.socialmedia.repository.CommentRepository;
import com.example.socialmedia.requests.CreateCommentRequest;
import com.example.socialmedia.requests.UpdateCommentRequest;
import com.example.socialmedia.responses.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public Page<CommentResponse> getAllComments(Optional<Long> userId, Optional<Long> postId, Pageable pageable) {
        Page<Comment> comments;
        if (userId.isPresent() && postId.isPresent()) {
            comments = commentRepository.findByUserIdAndPostId(userId.get(), postId.get(), pageable);
        } else if (userId.isPresent()) {
            comments = commentRepository.findByUserId(userId.get(), pageable);
        } else if (postId.isPresent()) {
            comments = commentRepository.findByPostId(postId.get(), pageable);
        } else {
            comments = commentRepository.findAll(pageable);
        }
        return comments.map(CommentResponse::new);
    }

    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        return new CommentResponse(comment);
    }

    public CommentResponse createComment(CreateCommentRequest request) {
        User user = userService.findUserOrThrow(request.getUserId());
        Post post = postService.getRawPostOrThrow(request.getPostId());
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(request.getText());
        return new CommentResponse(commentRepository.save(comment));
    }

    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        if (request.getText() != null) comment.setText(request.getText());
        return new CommentResponse(commentRepository.save(comment));
    }

    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment", commentId);
        }
        commentRepository.deleteById(commentId);
    }
}
