package com.finance.dashboard.auth;

import com.finance.dashboard.common.exception.BadRequestException;
import com.finance.dashboard.security.JwtService;
import com.finance.dashboard.security.UserDetailsImpl;
import com.finance.dashboard.user.RoleName;
import com.finance.dashboard.user.User;
import com.finance.dashboard.user.UserMapper;
import com.finance.dashboard.user.UserRepository;
import com.finance.dashboard.user.dto.AuthResponse;
import com.finance.dashboard.user.dto.LoginRequest;
import com.finance.dashboard.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .createdAt(Instant.now())
                .roles(EnumSet.of(RoleName.VIEWER))
                .build();
        userRepository.save(user);
        UserDetailsImpl details = UserDetailsImpl.fromUser(user);
        String token = jwtService.generateToken(details);
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(UserMapper.toResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }
        if (!user.isActive()) {
            throw new BadRequestException("Account is disabled");
        }
        UserDetailsImpl details = UserDetailsImpl.fromUser(user);
        String token = jwtService.generateToken(details);
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(UserMapper.toResponse(user))
                .build();
    }
}
