package com.cdac.dreamblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.dto.request.UserLoginRequestDto;
import com.cdac.dreamblog.dto.request.UserRequestDto;
import com.cdac.dreamblog.dto.response.UserLoginResponseDto;
import com.cdac.dreamblog.dto.response.UserResponseDto;
import com.cdac.dreamblog.service.implementation.UserServiceImplementation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  UserServiceImplementation userService;

  // Sign-up
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto userRequestDto) {
    try {
      UserResponseDto createdUser = userService.create(userRequestDto);
      return new ResponseEntity<>(createdUser, HttpStatus.CREATED); // 201 Created
    } catch (IllegalArgumentException e) {
      // Catches validation errors or unique constraint violations from the service
      
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
    } catch (Exception e) {
      // Catch-all for any other unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  // Sign-in
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
    try {
      // Attempt to authenticate the user
      UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto);
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(userLoginResponseDto);

    } catch (BadCredentialsException ex) {
      // Incorrect username or password
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid username or password");
    } catch (org.springframework.security.core.AuthenticationException ex) {
      // Any other authentication-related exception
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ex.getMessage());
    } catch (Exception ex) {
      // Generic exception handler
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred during login");
    }
  }

}
