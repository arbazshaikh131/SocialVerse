package com.example.socialmedia.repository;

import com.example.socialmedia.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByUserId(Long userId, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(
        String title, String text, Pageable pageable);

    @Query(value = "SELECT id FROM post WHERE user_id = :userId ORDER BY created_at DESC LIMIT 5",
           nativeQuery = true)
    List<Long> findTopPostIdsByUserId(@Param("userId") Long userId);
}
