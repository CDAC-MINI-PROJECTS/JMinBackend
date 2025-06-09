package com.cdac.dreamblog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.cdac.dreamblog.dto.response.CommentResponseDto;
import com.cdac.dreamblog.model.DreamReaction;

@Data
public class DreamWithCommentsDto {
    private Long dreamId;
    private String content;
    private String title;
    private LocalDateTime createdAt;
    private List<DreamReaction> reactions;
    private String visibility;
    private String location;
    private UserMinimalDto user; // Minimal details of the dream owner
    private List<CommentResponseDto> comments; // List of associated comments
}
