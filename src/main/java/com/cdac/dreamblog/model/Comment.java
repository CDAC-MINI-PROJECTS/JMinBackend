package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String commentText;
    private LocalDateTime createdAt;

    private String visibility; // "public" or "private"

    @ManyToOne
    @JoinColumn(name = "dreamId")
    private Dream dream;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}