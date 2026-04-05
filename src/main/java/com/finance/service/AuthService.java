package com.finance.service;

import com.finance.exception.AppException;
import com.finance.model.User;
import com.finance.model.dto.AuthResponse;
import com.finance.model.dto.LoginRequest;
import com.finance.model.dto.RegisterRequest;
import com.finance.model.enums.Role;
import com.finance.model.enums.UserStatus;
import com.finance.repository.UserRepository;
import com.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new AppException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(HttpStatus.FORBIDDEN, "Account is inactive");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
