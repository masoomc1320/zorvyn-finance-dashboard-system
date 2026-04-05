package com.finance.dashboard.user;

import com.finance.dashboard.security.SecurityUtils;
import com.finance.dashboard.security.UserDetailsImpl;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isSelf(Long userId) {
        UserDetailsImpl u = SecurityUtils.requireCurrentUser();
        return u.getId().equals(userId);
    }
}
