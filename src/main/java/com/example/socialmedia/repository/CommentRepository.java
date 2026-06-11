package com.example.socialmedia.repository;

import com.example.socialmedia.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByUserId(Long userId, Pageable pageable);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    Page<Comment> findByUserIdAndPostId(Long userId, Long postId, Pageable pageable);

    @Query(value = "SELECT 'commented' AS action, c.post_id, u.profile_image_url, u.username " +
                   "FROM comment c LEFT JOIN users u ON u.id = c.user_id " +
                   "WHERE c.post_id IN :postIds LIMIT 5",
           nativeQuery = true)
    List<Object> findUserCommentsByPostIds(@Param("postIds") List<Long> postIds);
}
