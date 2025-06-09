package com.cdac.dreamblog.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {
  private Long userId;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
   
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

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
    
    private String role; 

    private Boolean isActive;

    @PastOrPresent(message = "Last login date must be in the past or present")
    private LocalDateTime lastLogin;

    // Social media URLs
    private String instagramURL;

    private String twitterURL;
    
    private String facebookURL;

    private String linkedinURL;

    private String secretKey;

    // Timestamps for auditing
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
