package com.cdac.dreamblog.controller;

import java.util.HashMap;
import java.util.Map;

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

import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.UserRepository;
import com.cdac.dreamblog.service.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired UserRepository userRepo;
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired AuthenticationManager authManager;
  @Autowired JwtUtil jwtUtil;

  @PostMapping("/register")
  public String register(@RequestBody User user) {
    System.out.println("Registering user: " + user);
    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
    userRepo.save(user);
    return "User registered";
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody User user) {
    try {
        // Attempt to authenticate the user
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPasswordHash())
        );

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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed");
    } catch (Exception ex) {
        // Generic exception handler
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during login");
    }
}

}
