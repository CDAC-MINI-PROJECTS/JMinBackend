package com.cdac.dreamblog.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.cdac.dreamblog.dto.UserDto;
import com.cdac.dreamblog.dto.request.UserRequestDto;
import com.cdac.dreamblog.dto.response.UserResponseDto;
import com.cdac.dreamblog.exception.ResourceNotFoundException;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.UserRepository;
import com.cdac.dreamblog.service.implementation.UserServiceImplementation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserServiceImplementation userService;

    // Create new user
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDto userDto) {
        try { 
            return ResponseEntity.ok();
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.CREATED).body("created successfully");
        }
    }

    // Get all users (admin-only if required)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get user by username
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setProfile(user.getProfile());
                    dto.setCover(user.getCover());
                    dto.setBio(user.getBio());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setCountry(user.getCountry());
                    dto.setCity(user.getCity());
                    dto.setState(user.getState());
                    dto.setZipCode(user.getZipCode());

                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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

    // Update user - only update allowed fields
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDto userRequestDto) {
            try {
              UserResponseDto userResponseDto = userService.update(userId, userRequestDto)   
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
    @DeleteMapping("/{username}")
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<?> softDeleteUser(@PathVariable String username) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        Long userId = userRepository.findByUsername(authenticatedUsername)
                .map(User::getUserId)
                .orElse(null);

        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setIsActive(false);
                    user.setUpdatedAt(LocalDateTime.now());
                    user.setUpdatedBy(userId);
                    userRepository.save(user);
                    return ResponseEntity.ok("User deactivated successfully.");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    // Activate user profile (admin only)
    @PutMapping("/activate/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setIsActive(true);
                    userRepository.save(user);
                    return ResponseEntity.ok("User activated successfully.");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }
}

// Rest

// localhost:8080/api/users

// Requesr Accpet / Response