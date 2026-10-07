package com.dev.smartlabbackend.service.impl;

import com.dev.smartlabbackend.dto.request.LoginRequest;
import com.dev.smartlabbackend.dto.response.LoginResponse;
import com.dev.smartlabbackend.dto.response.UserProfileResponse;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.UserStatus;
import com.dev.smartlabbackend.repository.UserRepository;
import com.dev.smartlabbackend.security.CustomUserDetails;
import com.dev.smartlabbackend.security.JwtTokenProvider;
import com.dev.smartlabbackend.service.AuthService;
import com.dev.smartlabbackend.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RedisTokenService redisTokenService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getOrgEmail().trim().toLowerCase();
        log.info("Attempting login for org_email: {}", email);

        User user = userRepository.findActiveByOrgEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found or deleted with email {}", email);
                    return new BadCredentialsException("Invalid org email or password");
                });

        if (UserStatus.LOCKED.equals(user.getStatus())) {
            log.warn("Login failed: Account is locked for user {}", email);
            throw new LockedException("Your account is locked. Please contact administrator");
        }

        if (UserStatus.INACTIVE.equals(user.getStatus())) {
            log.warn("Login failed: Account is inactive for user {}", email);
            throw new DisabledException("Your account is inactive. Please contact administrator");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: Password mismatch for user {}", email);
            throw new BadCredentialsException("Invalid org email or password");
        }

        CustomUserDetails userDetails = CustomUserDetails.build(user);
        String accessToken = tokenProvider.generateTokenFromUserDetails(userDetails);

        UserProfileResponse userProfile = UserProfileResponse.fromEntity(user);
        // Cache user profile in Redis for fast retrieval
        redisTokenService.cacheUserProfile(userProfile);

        log.info("User {} logged in successfully with role {}", email, user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs())
                .user(userProfile)
                .build();
    }

    @Override
    public void logout(String bearerToken) {
        if (!StringUtils.hasText(bearerToken)) {
            return;
        }

        String token = bearerToken;
        if (bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        if (tokenProvider.validateToken(token)) {
            long remainingTtl = tokenProvider.getRemainingExpirationMs(token);
            Long userId = tokenProvider.getUserIdFromToken(token);

            // Blacklist the token in Redis until it naturally expires
            redisTokenService.blacklistToken(token, remainingTtl);

            // Evict user profile cache in Redis
            if (userId != null) {
                redisTokenService.evictUserProfile(userId);
            }
            log.info("User with id {} logged out and token blacklisted", userId);
        }
    }
}
