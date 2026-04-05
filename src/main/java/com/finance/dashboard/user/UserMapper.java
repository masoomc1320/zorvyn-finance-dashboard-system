package com.finance.dashboard.user;

import com.finance.dashboard.user.dto.UserResponse;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .roles(Set.copyOf(user.getRoles()))
                .build();
    }
}
