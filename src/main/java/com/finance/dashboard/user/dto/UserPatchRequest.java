package com.finance.dashboard.user.dto;

import com.finance.dashboard.user.RoleName;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserPatchRequest {

    private Set<RoleName> roles;

    private Boolean active;

    @Size(max = 255)
    private String email;
}
