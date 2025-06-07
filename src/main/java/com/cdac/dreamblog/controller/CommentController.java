// src/main/java/com/cdac/dreamblog/controller/CommentController.java
package com.cdac.dreamblog.controller;

import com.cdac.dreamblog.dto.DreamMinimalDto;
import com.cdac.dreamblog.dto.UserMinimalDto;
import com.cdac.dreamblog.dto.request.CommentRequestDto;
import com.cdac.dreamblog.dto.response.CommentResponseDto;
import com.cdac.dreamblog.model.Comment;
import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.CommentRepository;
import com.cdac.dreamblog.repository.DreamRepository;
import com.cdac.dreamblog.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // Important for write operations

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final DreamRepository dreamRepository;
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository, DreamRepository dreamRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.dreamRepository = dreamRepository;
        this.userRepository = userRepository;
    }

    // --- Helper Methods for DTO Conversion ---
    // These methods replace the CommentMapper when skipping the service layer.
    private UserMinimalDto toUserMinimalDto(User user) {
        if (user == null) return null;
        UserMinimalDto dto = new UserMinimalDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        return dto;
    }

    private DreamMinimalDto toDreamMinimalDto(Dream dream) {
        if (dream == null) return null;
        DreamMinimalDto dto = new DreamMinimalDto();
        dto.setDreamId(dream.getDreamId());
        // Truncate content for minimal DTO
        dto.setContent(dream.getContent() != null && dream.getContent().length() > 50 ? dream.getContent().substring(0, 50) + "..." : dream.getContent());
        dto.setVisibility(dream.getVisibility());
        return dto;
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null) return null;
        CommentResponseDto dto = new CommentResponseDto();
        dto.setCommentId(comment.getCommentId());
        dto.setCommentText(comment.getCommentText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setVisibility(comment.getVisibility());
        dto.setDream(toDreamMinimalDto(comment.getDream()));
        dto.setUser(toUserMinimalDto(comment.getUser()));
        return dto;
    }

    /**
     * Creates a new comment.
     * Business logic directly handled in the controller.
     * @param requestDto The DTO containing comment text, dreamId, and userId.
     * @return ResponseEntity with the created comment.
     */
    @PostMapping
    @Transactional // Apply transactional for write operations
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequestDto requestDto) {
        try {
            // 1. Fetch associated Dream and User entities from repositories
            User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDto.getUserId()));

            Dream dream = dreamRepository.findById(requestDto.getDreamId())
                .orElseThrow(() -> new EntityNotFoundException("Dream not found with ID: " + requestDto.getDreamId()));

            // 2. Create Comment entity
            Comment comment = new Comment();
            comment.setCommentText(requestDto.getCommentText());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setDream(dream);
            comment.setUser(user);

            // Set visibility, defaulting to "public"
            comment.setVisibility(Optional.ofNullable(requestDto.getVisibility())
                                        .filter(v -> v.equals("public") || v.equals("private"))
                                        .orElse("public"));

            // 3. Save to database directly via repository
            Comment savedComment = commentRepository.save(comment);

            // 4. Convert and return DTO
            return new ResponseEntity<>(toCommentResponseDto(savedComment), HttpStatus.CREATED);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // For any custom validations like visibility
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves a comment by its ID.
     * Business logic directly handled in the controller.
     * @param id The ID of the comment.
     * @return ResponseEntity with the comment.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + id));
            return ResponseEntity.ok(toCommentResponseDto(comment));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves all comments for a specific dream.
     * Business logic directly handled in the controller.
     * @param dreamId The ID of the dream.
     * @param userRole (Optional) The role of the requesting user for visibility rules.
     * @return ResponseEntity with a list of comments.
     */
    @GetMapping("/dream/{dreamId}")
    public ResponseEntity<?> getCommentsByDream(@PathVariable Long dreamId,
                                                @RequestParam(required = false, defaultValue = "GUEST") String userRole) {
        try {
            Dream dream = dreamRepository.findById(dreamId)
                .orElseThrow(() -> new EntityNotFoundException("Dream not found with ID: " + dreamId));

            List<Comment> comments;
            // Basic visibility logic here (e.g., if user is admin, show all; otherwise, only public)
            if ("ADMIN".equalsIgnoreCase(userRole)) { // Example admin role check
                comments = commentRepository.findByDreamOrderByCreatedAtAsc(dream);
            } else {
                comments = commentRepository.findByDreamAndVisibilityOrderByCreatedAtAsc(dream, "public");
            }

            return ResponseEntity.ok(comments.stream()
                                            .map(this::toCommentResponseDto)
                                            .collect(Collectors.toList()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves all comments made by a specific user.
     * Business logic directly handled in the controller.
     * @param userId The ID of the user.
     * @return ResponseEntity with a list of comments.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCommentsByUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            List<Comment> comments = commentRepository.findByUserOrderByCreatedAtDesc(user);
            return ResponseEntity.ok(comments.stream()
                                            .map(this::toCommentResponseDto)
                                            .collect(Collectors.toList()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates an existing comment.
     * Business logic directly handled in the controller.
     * @param id The ID of the comment to update.
     * @param requestDto The DTO containing updated comment text, dreamId, and userId (userId must match original author for authorization).
     * @return ResponseEntity with the updated comment.
     */
    @PutMapping("/{id}")
    @Transactional // Apply transactional for write operations
    public ResponseEntity<?> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequestDto requestDto) {
        try {
            Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + id));

            User requestingUser = userRepository.findById(requestDto.getUserId())
                                 .orElseThrow(() -> new EntityNotFoundException("Requesting user not found with ID: " + requestDto.getUserId()));

            // Basic authorization check: Only the comment author can update
            if (!existingComment.getUser().getUserId().equals(requestingUser.getUserId())) {
                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this comment.");
            }

            existingComment.setCommentText(requestDto.getCommentText());
            // Update visibility if provided and valid
            Optional.ofNullable(requestDto.getVisibility())
                    .filter(v -> v.equals("public") || v.equals("private"))
                    .ifPresent(existingComment::setVisibility);

            Comment updatedComment = commentRepository.save(existingComment);
            return ResponseEntity.ok(toCommentResponseDto(updatedComment));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // e.g., invalid visibility
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Deletes a comment by its ID.
     * Business logic directly handled in the controller.
     * @param id The ID of the comment to delete.
     * @param requestingUserId The ID of the user attempting to delete the comment (for authorization).
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    @Transactional // Apply transactional for write operations
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                           @RequestParam(name = "requestingUserId") Long requestingUserId) { // Needs requesting user ID for auth
        try {
            Comment commentToDelete = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + id));

            User requestingUser = userRepository.findById(requestingUserId)
                                 .orElseThrow(() -> new EntityNotFoundException("Requesting user not found with ID: " + requestingUserId));

            // Basic authorization check: Only the comment author can delete (or an admin, if implemented)
            if (!commentToDelete.getUser().getUserId().equals(requestingUser.getUserId())) {
                // In a real app, you'd also check if requestingUser has ADMIN role here
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this comment.");
            }

            commentRepository.delete(commentToDelete);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // For authorization failures, etc.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}