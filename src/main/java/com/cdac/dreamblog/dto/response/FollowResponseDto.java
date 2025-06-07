package com.cdac.dreamblog.dto.response;

import java.time.LocalDateTime;

import com.cdac.dreamblog.dto.UserMinimalDto;

import lombok.Data;

@Data
public class FollowResponseDto {
    private UserMinimalDto follower;
    private UserMinimalDto followed; // Details of the user who is following
    private LocalDateTime followedAt;
}
