package com.cdac.dreamblog.service.implementation;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.dreamblog.dto.request.UserLoginRequestDto;
import com.cdac.dreamblog.dto.request.UserRequestDto;
import com.cdac.dreamblog.dto.response.UserLoginResponseDto;
import com.cdac.dreamblog.dto.response.UserResponseDto;
import com.cdac.dreamblog.enums.Role;
import com.cdac.dreamblog.exception.BadRequestException;
import com.cdac.dreamblog.exception.ResourceNotFoundException;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.UserRepository;
import com.cdac.dreamblog.service.IUserService;
import com.cdac.dreamblog.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImplementation implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtUtil jwtUtil;

    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setEmail(user.getEmail());
        userDto.setCountry(user.getCountry());
        userDto.setState(user.getState());
        userDto.setCity(user.getCity());
        userDto.setBio(user.getBio());
        userDto.setInstagramURL(user.getInstagramURL());
        userDto.setTwitterURL(user.getTwitterURL());
        userDto.setFacebookURL(user.getFacebookURL());
        userDto.setLinkedinURL(user.getLinkedinURL());
        userDto.setMaritalStatus(user.getMaritalStatus());
        userDto.setGender(user.getGender());
        userDto.setAddressLine1(user.getAddressLine1());
        return userDto;
    }

    public UserResponseDto create(UserRequestDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        if (userDto.getRole() != null && userDto.getRole().equals("ROLE_USER")) {
            user.setRole("ROLE_USER");
        }

        if (userDto.getRole() != null && userDto.getRole().equals("ROLE_ADMIN")) {
            user.setRole("ROLE_ADMIN");
        }
        User savedUser = userRepository.save(user);
        return toUserResponseDto(savedUser);
    }

    public UserLoginResponseDto login(UserLoginRequestDto userLoginDto) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));

        // Checking given password and username is correct or not
        if (!authentication.isAuthenticated()) {
            throw new BadRequestException("Username or password is incorrect");
        }

        userRepository.findByUsername(userLoginDto.getUsername()).map((user) -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            return user;
        });

        // Generate JWT token if authentication is successful
        String token = jwtUtil.generateToken(userLoginDto.getUsername());

        // Mapping with response dto
        UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto();
        userLoginResponseDto.setMessage("Login Successfully");
        userLoginResponseDto.setToken(token);

        return userLoginResponseDto;

    }

    public UserResponseDto update(Long userId, UserRequestDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (userDto.getUsername() != null && !userDto.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new IllegalArgumentException("Username already taken: " + userDto.getUsername());
            }
            existingUser.setUsername(userDto.getUsername());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Email already taken: " + userDto.getEmail());
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getDob() != null)
            existingUser.setDob(userDto.getDob());
        if (userDto.getFirstName() != null)
            existingUser.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null)
            existingUser.setLastName(userDto.getLastName());
        if (userDto.getProfile() != null)
            existingUser.setProfile(userDto.getProfile());
        if (userDto.getCover() != null)
            existingUser.setCover(userDto.getCover());
        if (userDto.getBio() != null)
            existingUser.setBio(userDto.getBio());

        if (userDto.getCountry() != null)
            existingUser.setCountry(userDto.getCountry());
        if (userDto.getCity() != null)
            existingUser.setCity(userDto.getCity());
        if (userDto.getState() != null)
            existingUser.setState(userDto.getState());
        if (userDto.getZipCode() != null)
            existingUser.setZipCode(userDto.getZipCode());

        if (userDto.getInstagramURL() != null)
            existingUser.setInstagramURL(userDto.getInstagramURL());
        if (userDto.getTwitterURL() != null)
            existingUser.setTwitterURL(userDto.getTwitterURL());
        if (userDto.getFacebookURL() != null)
            existingUser.setFacebookURL(userDto.getFacebookURL());
        if (userDto.getLinkedinURL() != null)
            existingUser.setLinkedinURL(userDto.getLinkedinURL());

        // if (userDto.getIsActive() != null)
        // existingUser.setIsActive(userDto.getIsActive());
        // if (userDto.getIsEmailVerified() != null)
        // existingUser.setIsEmailVerified(userDto.getIsEmailVerified());

        User updatedUser = userRepository.save(existingUser);
        UserResponseDto userResponseDto = toUserResponseDto(updatedUser);

        return userResponseDto;
    }

    @Transactional // Ensures the delete operation is performed within a transaction
    public void deleteUser(Long id) {
        // 1. Check if the user exists before attempting to delete
        if (!userRepository.existsById(id)) {
            // If the user doesn't exist, throw an EntityNotFoundException
            // The controller will then catch this and return a 404 Not Found response
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        // 2. If the user exists, proceed with the deletion
        userRepository.deleteById(id);
    }

    public UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username == null || username.isEmpty()) {
            throw new ResourceNotFoundException("Username is not found");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return toUserResponseDto(user);
    }

    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return toUserResponseDto(user);
    }

    public boolean activateUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        user.setIsActive(!user.getIsActive());
        return !user.getIsActive();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable); // Use the Pageable here
    }

}
