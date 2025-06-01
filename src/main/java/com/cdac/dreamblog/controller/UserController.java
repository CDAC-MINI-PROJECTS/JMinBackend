package com.cdac.dreamblog.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
    

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .<ResponseEntity<?>>map(user -> ResponseEntity.ok().body(user))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + id + " not found.")
            );
    }

    @GetMapping("/me")
  public ResponseEntity<?> getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   
    String username = authentication.getName();
System.out.println("Fetching user for username: " + authentication.getCredentials());
    if (username == null || username.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
    Optional<User> userOpt = userRepository.findByUsernameOrEmail(username, username);
    
    System.out.println("Fetched user: " + userOpt);
    return userOpt.map(user -> ResponseEntity.ok(user))
     .<ResponseEntity<?>>map(user -> ResponseEntity.ok().body(user))
                  .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
  }
}
