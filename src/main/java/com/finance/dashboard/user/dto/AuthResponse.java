package com.finance.dashboard.user.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {

    String accessToken;
    String tokenType;
    UserResponse user;
}
