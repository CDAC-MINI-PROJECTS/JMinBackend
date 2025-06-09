package com.cdac.dreamblog.dto.request;

import lombok.Data;

@Data
public class ReactionRequestDto {
  private Long userId;
  private String reactionType;
}