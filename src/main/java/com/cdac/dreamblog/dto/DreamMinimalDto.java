// src/main/java/com/cdac/dreamblog/dto/DreamMinimalDto.java
package com.cdac.dreamblog.dto;

import lombok.Data;

// A minimal DTO for Dream details to be nested in CommentResponseDto
@Data
public class DreamMinimalDto {
    private Long dreamId;
    private String content; // A snippet of the content
    private String visibility;
}