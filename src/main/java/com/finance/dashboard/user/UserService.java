package com.finance.dashboard.user;

import com.finance.dashboard.common.exception.BadRequestException;
import com.finance.dashboard.common.exception.ResourceNotFoundException;
import com.finance.dashboard.user.dto.AdminCreateUserRequest;
import com.finance.dashboard.user.dto.UserPatchRequest;
import com.finance.dashboard.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createByAdmin(AdminCreateUserRequest request) {
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
                .active(request.isActive())
                .createdAt(Instant.now())
                .roles(new HashSet<>(request.getRoles()))
                .build();
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponse patch(Long id, UserPatchRequest request) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.getRoles() != null) {
            user.getRoles().clear();
            user.getRoles().addAll(request.getRoles());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }
        return UserMapper.toResponse(userRepository.save(user));
    }
}
