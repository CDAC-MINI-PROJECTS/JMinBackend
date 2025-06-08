package com.cdac.dreamblog.repository;

import com.cdac.dreamblog.model.Comment;
import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for a specific dream, ordered by creation time
    List<Comment> findByDreamOrderByCreatedAtAsc(Dream dream);

    // Find all comments made by a specific user, ordered by creation time
    List<Comment> findByUserOrderByCreatedAtDesc(User user);

    // Find comments for a dream with public visibility
    List<Comment> findByDreamAndVisibilityOrderByCreatedAtAsc(Dream dream, String visibility);
}