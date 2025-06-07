package com.cdac.dreamblog.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.dto.UserDto;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.UserRepository;
import com.cdac.dreamblog.service.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired UserRepository userRepo;
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired AuthenticationManager authManager;
  @Autowired JwtUtil jwtUtil;

  @PostMapping("/register")
  public String register(@Valid @RequestBody UserDto userDto) {
    System.out.println("Registering user: " + userDto.getUsername() + " " + userDto.getEmail());
    User user = new User();
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setFirstName(userDto.getFirstName());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(userDto.getRole());
    userRepo.save(user);
    return "User registered";
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody User user) {
    try {

      System.out.println("Logging in user: " + user.getUsername() + user.getPassword());
        // Attempt to authenticate the user
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        System.out.println("Authentication successful for user: " + authentication);

        // Generate JWT token if authentication is successful
        String token = jwtUtil.generateToken(user.getUsername());

        // Optionally return the token in a standard structure
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);

    } catch (BadCredentialsException ex) {
        // Incorrect username or password
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    } catch (org.springframework.security.core.AuthenticationException ex) {
        // Any other authentication-related exception

        System.out.println("Authentication failed: " + ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    } catch (Exception ex) {
        // Generic exception handler
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during login");
    }
  }

  

}
