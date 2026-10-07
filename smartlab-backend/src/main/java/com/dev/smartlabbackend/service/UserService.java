package com.dev.smartlabbackend.service;

import com.dev.smartlabbackend.dto.request.UpdateProfileRequest;
import com.dev.smartlabbackend.dto.response.UserProfileResponse;
import com.dev.smartlabbackend.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(String orgEmail);

    UserProfileResponse getUserProfileById(Long userId);

    UserProfileResponse updateCurrentUserProfile(String orgEmail, UpdateProfileRequest request);

    Page<UserResponse> getListUser(int page, int size, String search);

    void deleteUser(Long currentId, Long userId);

    UserResponse softDeleteUser(Long currentId, Long userId);
}
