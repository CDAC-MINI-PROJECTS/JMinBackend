package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String bio;
    private String profilePictureUrl;
    // public enum Role {
    //     USER,
    //     ADMIN
    // }
    // @Enumerated(EnumType.STRING)
    private String role; // "user" or "admin"

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
