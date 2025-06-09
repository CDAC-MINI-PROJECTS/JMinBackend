package com.cdac.dreamblog.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.jsonwebtoken.lang.Objects;


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
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reactions_data", columnDefinition = "json") 
    List<DreamReaction> reactions = new ArrayList<>();

    private Boolean isReposted;

    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    /**
     * Adds or updates a reaction for a user. If the user already has a reaction
     * of the same type, it updates the timestamp. If the user has a different
     * reaction type, it replaces it.
     * @param userId The ID of the user performing the reaction.
     * @param reactionType The type of reaction (e.g., "like", "dislike", "cry", "best").
     */
    public void addOrUpdateReaction(Long userId, String reactionType) {
        System.out.println("Getting Inside addOrUpdateReaction"+ reactionType);
        if (reactions == null) {
            reactions = new ArrayList<>();
        }

        // Find if this user already reacted
        Optional<DreamReaction> existingReaction = reactions.stream()
            .filter(r -> r.getUserId().equals(userId))
            .findFirst();
        

        if (existingReaction.isPresent()) {
                System.out.println("Getting Inside if"+ reactionType);

            // User reacted before. Check if it's the same type
            // if (existingReaction.get().getType().equals(reactionType.toString())) {
            //     // Same type, just update timestamp (or do nothing if timestamp isn't important for updates)
            //     existingReaction.get().setTimestamp(LocalDateTime.now());
            // } else {
                // Different type, remove old and add new
                reactions.remove(existingReaction.get());
                reactions.add(new DreamReaction(userId, reactionType, LocalDateTime.now()));
            // }
            System.out.println("Getting Inside if");

        } else {
            System.out.println("Getting Inside elese");
            // New reaction from this user
            reactions.add(new DreamReaction(userId, reactionType, LocalDateTime.now()));
        }
    }

    /**
     * Removes a reaction from a user.
     * @param userId The ID of the user whose reaction to remove.
     */
    public void removeReaction(Long userId) {
        if (reactions != null) {
            reactions.removeIf(r -> r.getUserId().equals(userId));
        }
    }

    /**
     * Checks if a user has reacted to this dream.
     * @param userId The ID of the user.
     * @return true if the user has any reaction, false otherwise.
     */
    public boolean hasUserReacted(Long userId) {
        return reactions != null && reactions.stream().anyMatch(r -> r.getUserId().equals(userId));
    }

    /**
     * Gets the count for a specific reaction type.
     * @param type The reaction type (e.g., "like").
     * @return The count of reactions of that type.
     */
    public long getReactionCount(String type) {
        if (reactions == null) {
            return 0;
        }
        return reactions.stream()
            .filter(r -> r.getType().equalsIgnoreCase(type))
            .count();
    }

    /**
     * Gets the total count of all reactions.
     */
    public int getTotalReactionCount() {
        return reactions != null ? reactions.size() : 0;
    }
}