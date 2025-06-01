package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

// TODO: Add Validation annotations for fields for each entity

@Data
@Entity
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dreamId;

    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;
    private String content;
    private String tags; // Comma-separated
    private String visibility; // "public" or "private"
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}