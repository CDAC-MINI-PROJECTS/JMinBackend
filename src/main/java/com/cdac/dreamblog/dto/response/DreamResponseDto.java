package com.cdac.dreamblog.dto.response;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class DreamResponseDto {
    private Long dreamId;
    private String title;
    private String content;
    private String tags; 
    private String visibility;
    private Integer likeCount; 
    private Integer dislikeCount;
    private Boolean isReposted;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private UserResponseDto user;

    public UserResponseDto getUser(){
        return this.user;
    }

    public void setUser(UserResponseDto user){
        this.user = user;
    }
}
