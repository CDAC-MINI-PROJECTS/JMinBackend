package com.cdac.dreamblog.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cdac.dreamblog.enums.*;

import lombok.Data;

// Note: Refer https://jakarta.ee/specifications/bean-validation/3.0/ to add validation and other models

/**
 * Represents a user in the DreamBlog application.
 * Contains user details such as username, email, password hash, and profile information.
 */
@Data
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;
   
    @Column(nullable = false, unique = true)
    private String email;

    // Password should be stored as a hash, not plain text
    private String password;

    //Personal Information
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String profile;
    private String cover;
    private String gender;
    private String maritalStatus;
    private String bloodGroup;

    // Address Information
    private String country;
    private String state;
    private String city;
    private String addressLine1;
    private String addressLine2;
    private String zipCode;

    //Contact Information
    private String phoneNumber;
    private Boolean isEmailVerified;

    private String bio;
    private String language; // URL to the user's profile picture
    
    // Account details
    private String role;
    private Boolean isActive;
    private LocalDateTime lastLogin;

    // Social media URLs
    private String instagramURL;
    private String twitterURL;
    private String facebookURL;
    private String linkedinURL;

    // Timestamps for auditing
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
