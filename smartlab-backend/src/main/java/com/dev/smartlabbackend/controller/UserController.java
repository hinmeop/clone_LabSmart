package com.dev.smartlabbackend.controller;

import com.dev.smartlabbackend.common.response.ApiResponse;
import com.dev.smartlabbackend.dto.request.UpdateProfileRequest;
import com.dev.smartlabbackend.dto.response.DomainResponse;
import com.dev.smartlabbackend.dto.response.UserProfileResponse;
import com.dev.smartlabbackend.dto.response.UserResponse;
import com.dev.smartlabbackend.security.CustomUserDetails;
import com.dev.smartlabbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Profile", description = "Endpoints for retrieving and updating user profile")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(
            summary = "Get Current User Profile",
            description = "Get detailed profile information of the currently authenticated user (retrieved from Redis cache or PostgreSQL)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = userService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    @Operation(
            summary = "Update Current User Profile",
            description = "Update full name, phone, department, note, date of birth, and avatar url. Evicts and updates Redis cache."
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updatedProfile = userService.updateCurrentUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updatedProfile));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get User Profile By ID",
            description = "Retrieve user profile by unique database identifier"
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable("id") Long id) {
        UserProfileResponse profile = userService.getUserProfileById(id);
        return ResponseEntity.ok(ApiResponse.ok("User retrieved successfully", profile));
    }

    @GetMapping()
    @Operation(
            summary = "Get List User Profile",
            description = "It Admin/Manager view list domain"
    )
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getListUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        Page<UserResponse> response = userService.getListUser(page, size, search);
        return ResponseEntity.ok(ApiResponse.ok("Get list user successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft Delete User", description = "IT Admin/Manager soft delete user")
    public ResponseEntity<ApiResponse<UserResponse>> softDeleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        UserResponse response = userService.softDeleteUser(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("User soft deleted successfully", response));
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Delete User", description = "IT Admin/Manager delete user")
    public ResponseEntity<ApiResponse<Void>> hardDeleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        userService.deleteUser(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("User permanently deleted", null));
    }
}
