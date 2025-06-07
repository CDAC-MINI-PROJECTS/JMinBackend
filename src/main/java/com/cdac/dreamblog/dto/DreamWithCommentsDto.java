package com.cdac.dreamblog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.cdac.dreamblog.dto.response.CommentResponseDto;

@Data
public class DreamWithCommentsDto {
    private Long dreamId;
    private String content;
    private LocalDateTime createdAt;
    private String visibility;
    private String location;
    private UserMinimalDto user; // Minimal details of the dream owner
    private List<CommentResponseDto> comments; // List of associated comments
}
