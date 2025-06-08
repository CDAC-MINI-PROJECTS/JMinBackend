// src/main/java/com/cdac/dreamblog/controller/FollowController.java
package com.cdac.dreamblog.controller;

import com.cdac.dreamblog.dto.UserMinimalDto;
import com.cdac.dreamblog.dto.request.FollowRequestDto;
import com.cdac.dreamblog.dto.response.FollowResponseDto;
import com.cdac.dreamblog.dto.response.FollowerResponseDto;
import com.cdac.dreamblog.dto.response.FollowingResponseDto;
import com.cdac.dreamblog.model.Follow;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.FollowRepository;
import com.cdac.dreamblog.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // Apply transactional at controller if needed

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowRepository followRepository;
    private final UserRepository userRepository; // Inject UserRepository as well

    public FollowController(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    // --- Utility Methods (moved from Service, simplified for direct use) ---
    // In a real application, you'd likely still have a Mapper or utility class for DTO conversion.
    // For this example, we'll implement simple conversions directly.

    private UserMinimalDto toUserMinimalDto(User user) {
        if (user == null) return null;
        UserMinimalDto dto = new UserMinimalDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        return dto;
    }

    private FollowResponseDto toFollowResponseDto(Follow follow) {
        if (follow == null) return null;
        FollowResponseDto dto = new FollowResponseDto();
        dto.setFollower(toUserMinimalDto(follow.getFollower()));
        dto.setFollowed(toUserMinimalDto(follow.getFollower()));
        dto.setFollowedAt(follow.getFollowedAt());
        return dto;
    }

    private FollowerResponseDto toFollowerResponseDto(Follow follow) {
        if (follow == null) return null;
        FollowerResponseDto dto = new FollowerResponseDto();
        dto.setFollower(toUserMinimalDto(follow.getFollower()));
        dto.setUserId(follow.getFollowed().getUserId());
        dto.setUsername(follow.getFollowed().getUsername());
        dto.setFollowedAt(follow.getFollowedAt());
        return dto;
    }

    private FollowingResponseDto toFollowingResponseDto(Follow follow) {
        if (follow == null) return null;
        FollowingResponseDto dto = new FollowingResponseDto();
        dto.setFollowed(toUserMinimalDto(follow.getFollowed()));
        dto.setUserId(follow.getFollower().getUserId());
        dto.setUsername(follow.getFollower().getUsername());
        dto.setFollowedAt(follow.getFollowedAt());
        return dto;
    }


    /**
     * Endpoint to follow a user.
     * Business logic moved directly into the controller.
     * @param requestDto Contains followerId and followedId.
     * @return ResponseEntity with the created FollowResponseDto or an error message.
     */
    @PostMapping
    @Transactional // Transactional logic moved here
    public ResponseEntity<?> followUser(@Valid @RequestBody FollowRequestDto requestDto) {
        try {
            // 1. Fetch User entities
            User follower = userRepository.findById(requestDto.getFollowerId())
                .orElseThrow(() -> new EntityNotFoundException("Follower not found with ID: " + requestDto.getFollowerId()));

            User followed = userRepository.findById(requestDto.getFollowedId())
                .orElseThrow(() -> new EntityNotFoundException("User to follow not found with ID: " + requestDto.getFollowedId()));

            // 2. Validate: Cannot follow self
            if (follower.getUserId().equals(followed.getUserId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User cannot follow themselves.");
            }

            // 3. Validate: Already following
            if (followRepository.existsByFollowerAndFollowed(follower, followed)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User " + follower.getUsername() + " is already following " + followed.getUsername());
            }

            // 4. Create Follow entity
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            follow.setFollowedAt(LocalDateTime.now());

            // 5. Save and return DTO
            Follow savedFollow = followRepository.save(follow);
            return new ResponseEntity<>(toFollowResponseDto(savedFollow), HttpStatus.CREATED); // 201 Created

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    /**
     * Endpoint to unfollow a user.
     * Business logic moved directly into the controller.
     * @param followerId The ID of the user performing the unfollow.
     * @param followedId The ID of the user being unfollowed.
     * @return ResponseEntity with no content if successful, or an error.
     */
    @DeleteMapping("/{followerId}/{followedId}")
    @Transactional // Transactional logic moved here
    public ResponseEntity<?> unfollowUser(@PathVariable Long followerId, @PathVariable Long followedId) {
        try {
            // 1. Fetch User entities
            User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found with ID: " + followerId));

            User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("User to unfollow not found with ID: " + followedId));

            // 2. Find the specific follow relationship
            Follow follow = followRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new EntityNotFoundException("Follow relationship does not exist."));

            // 3. Delete the relationship
            followRepository.delete(follow);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    /**
     * Endpoint to get all users that a specific user is following.
     * Business logic moved directly into the controller.
     * @param userId The ID of the user whose following list is requested.
     * @return ResponseEntity with a list of FollowResponseDto.
     */
    @GetMapping("/following/{userId}")
    public ResponseEntity<?> getFollowing(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            List<Follow> followingRelations = followRepository.findByFollower(user);
            List<FollowingResponseDto> followingDtos = followingRelations.stream()
                .map(this::toFollowingResponseDto) // Use the local conversion method
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(followingDtos); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to get all users who are following a specific user.
     * Business logic moved directly into the controller.
     * @param userId The ID of the user whose followers list is requested.
     * @return ResponseEntity with a list of FollowResponseDto.
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            List<Follow> followerRelations = followRepository.findByFollowed(user);
            List<FollowerResponseDto> followerDtos = followerRelations.stream()
                .map(this::toFollowerResponseDto) // Use the local conversion method
                .collect(Collectors.toList());
            return ResponseEntity.ok(followerDtos); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to check if one user is following another.
     * Business logic moved directly into the controller.
     * @param followerId The ID of the potential follower.
     * @param followedId The ID of the potential followed user.
     * @return ResponseEntity with a boolean indicating follow status.
     */
    @GetMapping("/isFollowing/{followerId}/{followedId}")
    public ResponseEntity<?> isFollowing(@PathVariable Long followerId, @PathVariable Long followedId) {
        try {
            User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found with ID: " + followerId));
            User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("User to check not found with ID: " + followedId));

            boolean isFollowing = followRepository.existsByFollowerAndFollowed(follower, followed);
            return ResponseEntity.ok(isFollowing); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to get the count of followers for a user.
     * Business logic moved directly into the controller.
     * @param userId The ID of the user.
     * @return ResponseEntity with the follower count.
     */
    @GetMapping("/followers/count/{userId}")
    public ResponseEntity<?> getFollowerCount(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            long count = followRepository.countByFollowed(user);
            return ResponseEntity.ok(count); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to get the count of users a user is following.
     * Business logic moved directly into the controller.
     * @param userId The ID of the user.
     * @return ResponseEntity with the following count.
     */
    @GetMapping("/following/count/{userId}")
    public ResponseEntity<?> getFollowingCount(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            long count = followRepository.countByFollower(user);
            return ResponseEntity.ok(count); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}