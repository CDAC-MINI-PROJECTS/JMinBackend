package com.cdac.dreamblog.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.management.relation.Role;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long userId;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
   
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

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
    private String phoneNumber;

    private String secondaryEmail;

    private Boolean isEmailVerified;

    private String bio;

    private String language; // URL to the user's profile picture
    
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
