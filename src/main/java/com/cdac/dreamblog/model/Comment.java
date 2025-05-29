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

    @ManyToOne
    @JoinColumn(name = "dream_id")
    private Dream dream;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}