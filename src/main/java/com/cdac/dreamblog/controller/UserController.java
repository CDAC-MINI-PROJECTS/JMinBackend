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
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Create new user
    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDto userDto) {
        // Convert UserDto to User entity
        // User user = new User();
        //  if (userDto.getUsername().equals("admin")) {
        //     throw new ResourceNotFoundException("User 'admin' already exists.");
        // }
        // user.setUsername(userDto.getUsername());
        // user.setEmail(userDto.getEmail());
        // user.setPassword(userDto.getPassword()); // Ensure password is hashed in service layer
        // user.setProfile(userDto.getProfile());
        // user.setCover(userDto.getCover());
        // user.setBio(userDto.getBio());
        // user.setFirstName(userDto.getFirstName());
        // user.setLastName(userDto.getLastName());
        // user.setCountry(userDto.getCountry());
        // user.setCity(userDto.getCity());
        // user.setState(userDto.getState());
        // user.setProfile(userDto.getProfile());
        // user.setCover(userDto.getCover());
        // user.setZipCode(userDto.getZipCode());
        // user.setDob(userDto.getDob());
        // user.setInstagram_url(userDto.getInstagram_url());
        // user.setTwitter_url(userDto.getTwitter_url());
        // user.setFacebook_url(userDto.getFacebook_url());
        // user.setLinkedin_url(userDto.getLinkedin_url());
        // user.setRole(userDto.getRole());
        // user.setIsActive(true); // Default to active
        // user.setIsEmailVerified(false); // Default to not verified
        // user.setCreatedAt(LocalDateTime.now());
        // user.setUpdatedAt(LocalDateTime.now());
        // user.setCreatedBy(null); // Set to null or current user ID if available
        // user.setUpdatedBy(null); // Set to null or current user ID if available
        // Set other fields as needed
        // User createdUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("created successfully");
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
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setProfile(user.getProfile());
                    dto.setCover(user.getCover());
                    dto.setBio(user.getBio());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get current authenticated user
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        return userRepository.findByUsernameOrEmail(username, username)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    // Update user - only update allowed fields
    @PutMapping(value = "/{username}", consumes = "application/json")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @Valid @RequestBody User userData) {

        return userRepository.findByUsername(username)
                .<ResponseEntity<?>>map(existingUser -> {
                    if (userData.getProfile() != null)
                        existingUser.setProfile(userData.getProfile());
                    if (userData.getCover() != null)
                        existingUser.setCover(userData.getCover());
                    if (userData.getBio().isBlank() || userData.getBio() == null)
                        existingUser.setBio(userData.getBio());
                    if (userData.getFirstName() != null)
                        existingUser.setFirstName(userData.getFirstName());
                    if (userData.getLastName() != null)
                        existingUser.setLastName(userData.getLastName());
                    if (userData.getCountry() != null)
                        existingUser.setCountry(userData.getCountry());
                    if (userData.getCity() != null)
                        existingUser.setCity(userData.getCity());
                    if (userData.getState() != null)
                        existingUser.setState(userData.getState());
                    if (userData.getZipCode() != null)
                        existingUser.setZipCode(userData.getZipCode());
                    if (userData.getDob() != null)
                        existingUser.setDob(userData.getDob());
                    if (userData.getEmail() != null)
                        existingUser.setEmail(userData.getEmail());
                    if (userData.getUsername() != null)
                        existingUser.setUsername(userData.getUsername());

                    if (userData.getInstagram_url() != null)
                        existingUser.setInstagram_url(userData.getInstagram_url());
                    if (userData.getTwitter_url() != null)
                        existingUser.setTwitter_url(userData.getTwitter_url());
                    if (userData.getFacebook_url() != null)
                        existingUser.setFacebook_url(userData.getFacebook_url());
                    if (userData.getLinkedin_url() != null)
                        existingUser.setLinkedin_url(userData.getLinkedin_url());

                    if (userData.getRole() != null)
                        existingUser.setRole(userData.getRole());
                    if (userData.getIsActive() != null)
                        existingUser.setIsActive(userData.getIsActive());
                    if (userData.getIsEmailVerified() != null)
                        existingUser.setIsEmailVerified(userData.getIsEmailVerified());

                    User updatedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
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
