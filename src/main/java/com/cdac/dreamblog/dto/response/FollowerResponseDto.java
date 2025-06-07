package com.cdac.dreamblog.dto.response;

import java.time.LocalDateTime;

import com.cdac.dreamblog.dto.UserMinimalDto;

import lombok.Data;

@Data
public class FollowerResponseDto {
    private Long userId;
    private String username; // Details of the user being followed
    private UserMinimalDto follower; // Details of the user who is following
    private LocalDateTime followedAt;
}