// src/main/java/com/cdac/dreamblog/controller/CommentController.java
package com.cdac.dreamblog.controller;

import com.cdac.dreamblog.dto.request.CommentRequestDto;
import com.cdac.dreamblog.dto.response.CommentResponseDto;
import com.cdac.dreamblog.service.implementation.CommentServiceImplementation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // Important for write operations

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    CommentServiceImplementation commentService;

    /**
     * Creates a new comment.
     * 
     * @param requestDto The DTO containing comment text, dreamId, and userId.
     * @return ResponseEntity with the created comment.
     */
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequestDto requestDto) {
        try {
          CommentResponseDto commentResponseDto =  commentService.createComment(requestDto);
          return ResponseEntity.ok(commentResponseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // For any custom validations like visibility
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves a comment by its ID.
     * @param id The ID of the comment.
     * @return ResponseEntity with the comment.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            CommentResponseDto commentResponseDto = commentService.getCommentById(id);
            return ResponseEntity.ok(commentResponseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves all comments for a specific dream.
     * @param dreamId  The ID of the dream.
     * @param userRole (Optional) The role of the requesting user for visibility
     *                 rules.
     * @return ResponseEntity with a list of comments.
     */
    @GetMapping("/dream/{dreamId}")
    public ResponseEntity<?> getCommentsByDream(@PathVariable Long dreamId,
            @RequestParam(required = false, defaultValue = "GUEST") String userRole) {
        try {
            List<CommentResponseDto> commentResponseDto = commentService.getCommentByDream(dreamId);
            return ResponseEntity.ok(commentResponseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves all comments made by a specific user.
     * @param userId The ID of the user.
     * @return ResponseEntity with a list of comments.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCommentsByUser(@PathVariable Long userId) {
        try {
            List<CommentResponseDto> commentResponseDto = commentService.getCommentByUser(userId);
            return ResponseEntity.ok(commentResponseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates an existing comment.
     * @param id         The ID of the comment to update.
     * @param requestDto The DTO containing updated comment text, dreamId, and
     *                   userId (userId must match original author for
     *                   authorization).
     * @return ResponseEntity with the updated comment.
     */
    @PutMapping("/{id}")
    @Transactional // Apply transactional for write operations
    public ResponseEntity<?> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequestDto requestDto) {
        try {

            CommentResponseDto updatedComment = commentService.updateComment(id, requestDto);
            return ResponseEntity.ok(updatedComment);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // e.g., invalid visibility
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Deletes a comment by its ID.
     * @param id               The ID of the comment to delete.
     * @param requestingUserId The ID of the user attempting to delete the comment
     *                         (for authorization).
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    @Transactional // Apply transactional for write operations
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
            @RequestParam() Long requestingUserId) { // Needs requesting user ID for auth
        try {
            commentService.deleteComment(id, requestingUserId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // For authorization failures, etc.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}