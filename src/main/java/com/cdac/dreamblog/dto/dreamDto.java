package com.cdac.dreamblog.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DreamDto {
    private Long dreamId;

    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 1000 characters")
    private String content;

    private String tags; // Comma-separated
    @Pattern(regexp = "^(public|private)$", message = "Visibility must be either 'public' or 'private'")
    private String visibility; // "public" or "private"
   
    private Integer likeCount; 
    private Integer dislikeCount;
    
    private Boolean isReposted;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public Long setUserId() {
        return userId;
    }
}
