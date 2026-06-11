package com.example.socialmedia.controllers;

import com.example.socialmedia.requests.UpdateUserRequest;
import com.example.socialmedia.responses.UserResponse;
import com.example.socialmedia.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profiles and follow system")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users (paginated)")
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return userService.getAllUsers(PageRequest.of(page, size, sort));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by username")
    public Page<UserResponse> searchUsers(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.searchUsers(username, PageRequest.of(page, size));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile (bio and profile image)")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        return userService.updateUserProfile(userId, request);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/follow/{targetId}")
    @Operation(summary = "Follow a user")
    public ResponseEntity<String> followUser(@PathVariable Long userId, @PathVariable Long targetId) {
        userService.followUser(userId, targetId);
        return ResponseEntity.ok("Now following user " + targetId);
    }

    @DeleteMapping("/{userId}/follow/{targetId}")
    @Operation(summary = "Unfollow a user")
    public ResponseEntity<String> unfollowUser(@PathVariable Long userId, @PathVariable Long targetId) {
        userService.unfollowUser(userId, targetId);
        return ResponseEntity.ok("Unfollowed user " + targetId);
    }

    @GetMapping("/{userId}/activity")
    @Operation(summary = "Get recent activity (likes and comments on user's posts)")
    public List<Object> getUserActivity(@PathVariable Long userId) {
        return userService.getUserActivity(userId);
    }
}
