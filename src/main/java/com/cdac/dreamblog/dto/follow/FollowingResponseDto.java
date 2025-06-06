package com.cdac.dreamblog.dto.follow;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FollowingResponseDto {
    private Long userId;
    private String username; // Details of the user being followed
    private UserMinimalDto followed; // Details of the user who is following
    private LocalDateTime followedAt;
}
