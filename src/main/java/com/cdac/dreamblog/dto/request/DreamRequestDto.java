package com.cdac.dreamblog.dto.request;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class DreamRequestDto {
    private Long dreamId;
    private String title;
    private String content;
    private String tags; 
    private String visibility;
    private Integer likes; 
    private Integer dislikeCount;
    private Boolean isReposted;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private Long userId;
}
