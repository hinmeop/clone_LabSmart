package com.dev.smartlabbackend.service.impl;

import com.dev.smartlabbackend.dto.request.UpdateProfileRequest;
import com.dev.smartlabbackend.dto.response.UserProfileResponse;
import com.dev.smartlabbackend.dto.response.UserResponse;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.UserStatus;
import com.dev.smartlabbackend.exception.BadRequestException;
import com.dev.smartlabbackend.exception.ResourceNotFoundException;
import com.dev.smartlabbackend.mapper.UserMapper;
import com.dev.smartlabbackend.repository.UserRepository;
import com.dev.smartlabbackend.service.RedisTokenService;
import com.dev.smartlabbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RedisTokenService redisTokenService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String orgEmail) {
        log.info("Fetching profile for org_email: {}", orgEmail);

        // Fetch from database
        User user = userRepository.findActiveByOrgEmail(orgEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for email: " + orgEmail));

        // Check if cached in Redis
        UserProfileResponse cached = redisTokenService.getCachedUserProfile(user.getId());
        if (cached != null) {
            log.info("Returning profile for user {} from Redis cache", orgEmail);
            return cached;
        }

        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        // Save to Redis
        redisTokenService.cacheUserProfile(response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(Long userId) {
        log.info("Fetching profile for userId: {}", userId);

        UserProfileResponse cached = redisTokenService.getCachedUserProfile(userId);
        if (cached != null) {
            log.info("Returning profile for userId {} from Redis cache", userId);
            return cached;
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        redisTokenService.cacheUserProfile(response);
        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse updateCurrentUserProfile(String orgEmail, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", orgEmail);

        User user = userRepository.findActiveByOrgEmail(orgEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + orgEmail));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment().trim());
        }
        if (request.getNote() != null) {
            user.setNote(request.getNote().trim());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        User savedUser = userRepository.save(user);
        UserProfileResponse response = UserProfileResponse.fromEntity(savedUser);

        // Update Redis cache with fresh updated profile
        redisTokenService.cacheUserProfile(response);

        log.info("Successfully updated profile for user {}", orgEmail);
        return response;
    }

    @Transactional
    @Override
    public Page<UserResponse> getListUser(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        if (search != null && !search.isEmpty()) {
            users = userRepository.findByFullNameContainingIgnoreCase(search, pageable);
        }
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteUser(Long currentId, Long userId) {
        if (currentId.equals(userId)) {
            throw new BadRequestException("You cannot delete your own account");
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User updatedBy = userRepository.findByIdAndDeletedAtIsNull(currentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentId));

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setUpdatedBy(updatedBy);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse softDeleteUser(Long currentId, Long userId) {
        if (currentId.equals(userId)) {
            throw new BadRequestException("You cannot delete your own account");
        }
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User updatedBy = userRepository.findByIdAndDeletedAtIsNull(currentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentId));
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setUpdatedBy(updatedBy);

        user = userRepository.save(user);
        return userMapper.softDeleteUser(user);
    }
}
