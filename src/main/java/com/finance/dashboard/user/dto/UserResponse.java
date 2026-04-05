package com.finance.dashboard.user.dto;

import com.finance.dashboard.user.RoleName;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class UserResponse {

    Long id;
    String username;
    String email;
    boolean active;
    Set<RoleName> roles;
}
