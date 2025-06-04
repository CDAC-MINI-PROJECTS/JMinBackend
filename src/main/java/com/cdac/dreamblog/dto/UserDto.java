package com.cdac.dreamblog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {

    private Long userId;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
   
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    // Password should be stored as a hash, not plain text
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,100}$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;

    //Personal Information
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    private String profile;
    private String cover;
    
    private String gender;

    private String maritalStatus;
    
    @Size(max = 3, message = "Blood group must be less than 3 characters")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Blood group must be one of A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    // Address Information
    
    private String country;
    private String state;
    private String city;
    private String addressLine1;
    private String addressLine2;
    
    private String zipCode;

    //Contact Information
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    @Email(message = "Secondary email should be valid")
    @Size(max = 100, message = "Secondary email must be less than 100 characters")
    private String secondaryEmail;

    private Boolean isEmailVerified;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;


    private String language; // URL to the user's profile picture
    
    // Account details
    private String role; // "user" or "admin"
    @NotNull(message = "Active status is mandatory")
    private Boolean isActive;

    @PastOrPresent(message = "Last login date must be in the past or present")
    private LocalDateTime lastLogin;

    // Social media URLs
    @Size(max = 255, message = "Instagram URL must be less than 255 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?instagram\\.com/[a-zA-Z0-9._%+-]+/?$", message = "Invalid Instagram URL")
    private String instagram_url;

    @Size(max = 255, message = "Twitter URL must be less than 255 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?twitter\\.com/[a-zA-Z0-9._%+-]+/?$", message = "Invalid Twitter URL")
    private String twitter_url;
    
    @Size(max = 255, message = "Facebook URL must be less than 255 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?facebook\\.com/[a-zA-Z0-9._%+-]+/?$", message = "Invalid Facebook URL")
    private String facebook_url;

    @Size(max = 255, message = "LinkedIn URL must be less than 255 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/in/[a-zA-Z0-9._%+-]+/?$", message = "Invalid LinkedIn URL")
    private String linkedin_url;

    // Timestamps for auditing
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
