package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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
    private LocalDateTime dob;
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
    private String secondaryEmail;
    private Boolean isEmailVerified;

    private String bio;
    private String language; // URL to the user's profile picture
    
    // Account details
    private String role; // "user" or "admin"
    private Boolean isActive;
    private LocalDateTime lastLogin;

    // Social media URLs
    private String instagram_url;
    private String twitter_url;
    private String facebook_url;
    private String linkedin_url;

    // Timestamps for auditing
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
