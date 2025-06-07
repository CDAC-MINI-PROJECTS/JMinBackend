package com.cdac.dreamblog.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowRequestDto {
    @NotNull(message = "Follower ID cannot be null")
    private Long followerId;

    @NotNull(message = "Followed ID cannot be null")
    private Long followedId;
}
