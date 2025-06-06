package com.cdac.dreamblog.dto.follow;

import java.time.LocalDateTime;

import com.cdac.dreamblog.dto.UserDto;

import lombok.Data;

@Data
public class FollowResponseDto {
    private UserDto follower; // Details of the user who is following
    private UserDto followed; // Details of the user being followed
    private LocalDateTime followedAt;
}