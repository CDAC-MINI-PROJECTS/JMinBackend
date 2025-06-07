package com.cdac.dreamblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Comment text cannot be empty")
    @Size(max = 500, message = "Comment text cannot exceed 500 characters")
    private String commentText;

    @NotNull(message = "Dream ID cannot be null")
    private Long dreamId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    // Optional: Allow client to set visibility, or always default in service
    // @Pattern(regexp = "public|private", message = "Visibility must be 'public' or 'private'")
    private String visibility = "public"; // Default to public if not provided
}