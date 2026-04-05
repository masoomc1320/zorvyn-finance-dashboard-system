package com.finance.dashboard.config;

import com.finance.dashboard.user.RoleName;
import com.finance.dashboard.user.User;
import com.finance.dashboard.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.EnumSet;

/**
 * Optional first-time admin user when {@code app.seed.enabled=true} and the database has no users.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeedRunner implements CommandLineRunner {

    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!appProperties.isEnabled()) {
            return;
        }
        if (userRepository.count() > 0) {
            return;
        }
        if (appProperties.getAdminUsername() == null
                || appProperties.getAdminUsername().isBlank()
                || appProperties.getAdminPassword() == null
                || appProperties.getAdminPassword().isBlank()) {
            log.warn("app.seed.enabled is true but admin username/password are missing; skipping seed");
            return;
        }
        String email = appProperties.getAdminEmail() != null && !appProperties.getAdminEmail().isBlank()
                ? appProperties.getAdminEmail()
                : appProperties.getAdminUsername() + "@localhost";
        User admin = User.builder()
                .username(appProperties.getAdminUsername().trim())
                .email(email.trim())
                .passwordHash(passwordEncoder.encode(appProperties.getAdminPassword()))
                .active(true)
                .createdAt(Instant.now())
                .roles(EnumSet.of(RoleName.ADMIN))
                .build();
        userRepository.save(admin);
        log.info("Seeded admin user '{}'", admin.getUsername());
    }
}
