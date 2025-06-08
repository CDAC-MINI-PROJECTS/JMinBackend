package com.cdac.dreamblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cdac.dreamblog.dto.request.UserRequestDto;
import com.cdac.dreamblog.dto.response.UserResponseDto;
import com.cdac.dreamblog.exception.ResourceNotFoundException;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.service.implementation.UserServiceImplementation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserServiceImplementation userService;

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
        @PageableDefault(
            page = 0, // Default page number
            size = 10, // Default page size
            sort = "userId", // Default sorting field
            direction = Sort.Direction.ASC // Default sorting direction
        ) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    // Update user - only update allowed fields
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @Valid @PathVariable Long userId,
            @Valid @RequestBody UserRequestDto userRequestDto) {
            try {
              UserResponseDto userResponseDto = userService.update(userId, userRequestDto);  
              return ResponseEntity.ok(userResponseDto);
            } catch (IllegalArgumentException e) {
              // Catches validation errors or unique constraint violations from the service
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
            } catch (Exception e) {
              // Catch-all for any other unexpected errors
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An unexpected error occurred: " + e.getMessage());
            }
    }

    // Soft delete user (set isActive to false)
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@Valid @PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleted SuccussFully....");
        } catch (ResourceNotFoundException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 400 Bad Request
        } catch (IllegalArgumentException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            // Catch-all for any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Get current authenticated user
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser() {
        try {
            UserResponseDto userResponseDto = userService.getCurrentUser();
            return ResponseEntity.ok(userResponseDto);
        } catch (ResourceNotFoundException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 400 Bad Request
        } catch (IllegalArgumentException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            // Catch-all for any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@Valid @PathVariable String username) {
        try {
         UserResponseDto userResponseDto = userService.getUserByUsername(username);
        return ResponseEntity.ok(userResponseDto);
        } catch (ResourceNotFoundException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 400 Bad Request
        } catch (IllegalArgumentException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            // Catch-all for any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Activate/deactivate user profile (admin only)
    @PutMapping("/activate/{username}")
    public ResponseEntity<?> activateUser(@Valid @PathVariable String username) {
        try {
            boolean isActivated = userService.activateUser(username);
            if (!isActivated) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User DeActivated Successfully");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("User Activated Successfully");

        } catch (ResourceNotFoundException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 400 Bad Request
        } catch (IllegalArgumentException e) {
            // Catches validation errors or unique constraint violations from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            // Catch-all for any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}

