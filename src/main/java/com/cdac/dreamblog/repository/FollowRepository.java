package com.cdac.dreamblog.repository;

import com.cdac.dreamblog.model.Follow;
import com.cdac.dreamblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Follow.FollowId> {

    // Find a specific follow relationship
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    // Find all users that a specific user is following (i.e., 'follower' is the current user)
    List<Follow> findByFollower(User follower);

    // Find all users who are following a specific user (i.e., 'followed' is the current user)
    List<Follow> findByFollowed(User followed);

    // Check if a specific follow relationship exists
    boolean existsByFollowerAndFollowed(User follower, User followed);

    // Count followers for a user
    long countByFollowed(User followed);

    // Count following for a user
    long countByFollower(User follower);
}