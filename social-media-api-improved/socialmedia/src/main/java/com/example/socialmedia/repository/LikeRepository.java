package com.example.socialmedia.repository;

import com.example.socialmedia.entities.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Page<Like> findByUserId(Long userId, Pageable pageable);

    Page<Like> findByPostId(Long postId, Pageable pageable);

    Page<Like> findByUserIdAndPostId(Long userId, Long postId, Pageable pageable);

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    @Query(value = "SELECT 'liked' AS action, l.post_id, u.profile_image_url, u.username " +
                   "FROM post_like l LEFT JOIN users u ON u.id = l.user_id " +
                   "WHERE l.post_id IN :postIds LIMIT 5",
           nativeQuery = true)
    List<Object> findUserLikesByPostIds(@Param("postIds") List<Long> postIds);
}
