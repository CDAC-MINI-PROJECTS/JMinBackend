package com.cdac.dreamblog.dto.response;

import lombok.Data;

@Data
public class UserLoginResponseDto {
    private String token;
    private String message;
}
