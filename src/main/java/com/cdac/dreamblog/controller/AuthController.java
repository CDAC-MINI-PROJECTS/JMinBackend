package com.cdac.dreamblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
  public String login(@RequestBody User user) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPasswordHash()));
    return jwtUtil.generateToken(user.getUsername());
  }
}
