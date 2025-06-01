package com.cdac.dreamblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.dreamblog.model.Follow;

public interface FollowRepository extends JpaRepository<Follow, Follow.FollowId> {

    // Custom query methods can be added here if needed
    // For example, to find all followers of a user:
    // List<Follow> findByFollowed(User followed);
    
    // Or to find all users followed by a user:
    // List<Follow> findByFollower(User follower);

}