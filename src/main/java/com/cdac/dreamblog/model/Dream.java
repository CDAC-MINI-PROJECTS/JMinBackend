package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


// TODO: Add Validation annotations for fields for each entity

@Data
@Entity
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dreamId;

    private String title;
    
    @Lob
    @Column(name = "large_text_content", columnDefinition = "TEXT")
    private String content;

    private String tags; // Comma-separated
    private String visibility; // "public" or "private"
    private Integer likeCount; 
    private Integer dislikeCount;
    private Boolean isReposted;

    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}