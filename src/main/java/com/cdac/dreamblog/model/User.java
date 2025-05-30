package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
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
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
   
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is mandatory")
    private String email;

    private String passwordHash;
    private String fist_name;
    private String last_name;
    private Integer age;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;
    private String profilePictureUrl;
    
    private String role; // "user" or "admin"

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
