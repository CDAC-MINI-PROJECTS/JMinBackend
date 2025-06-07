package com.cdac.dreamblog.dto.comment;

import lombok.Data;
import java.time.LocalDateTime;

import com.cdac.dreamblog.dto.dream.DreamMinimalDto;
import com.cdac.dreamblog.dto.follow.UserMinimalDto;

@Data
public class CommentResponseDto {
    private Long commentId;
    private String commentText;
    private LocalDateTime createdAt;
    private String visibility;
    private DreamMinimalDto dream; // Details of the dream the comment belongs to
    private UserMinimalDto user;   // Details of the user who made the comment
}