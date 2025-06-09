package com.cdac.dreamblog.model;

import java.time.LocalDateTime; // Or Instant, ZonedDateTime depending on your time needs
import java.util.Objects;

public class DreamReaction {
    private Long userId;
    private String type; // e.g., "like", "dislike", "cry", "best"
    private LocalDateTime timestamp; // When the reaction occurred

    // Constructors
    public DreamReaction() {
        // Default constructor for Jackson (used by Hibernate's JSON type)
    }

    public DreamReaction(Long userId, String type, LocalDateTime timestamp) {
        this.userId = userId;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters and Setters (important for Hibernate/Jackson to read/write JSON)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DreamReaction that = (DreamReaction) o;

        // Use Objects.equals for null-safe comparison of objects
        // userId should ideally never be null for an instantiated object, but good practice to use Objects.equals
        return Objects.equals(userId, that.userId) &&
               Objects.equals(type, that.type); // <-- Fix: Using Objects.equals() handles nulls gracefully
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, type);
    }
}