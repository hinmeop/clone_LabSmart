package com.dev.smartlabbackend.controller;

import com.dev.smartlabbackend.common.response.ApiResponse;
import com.dev.smartlabbackend.dto.request.LoginRequest;
import com.dev.smartlabbackend.dto.response.LoginResponse;
import com.dev.smartlabbackend.dto.response.UserProfileResponse;
import com.dev.smartlabbackend.service.AuthService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, logout, and token authentication")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticate user with org_email and password. Returns JWT access token and user information."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid credentials or request validation failure"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Account is locked or inactive"
            )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login for user: {}", request.getOrgEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get Authenticated Profile",
            description = "Get detailed profile information of the currently authenticated user using JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized / Invalid or missing JWT token"
            )
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to get profile for authenticated user: {}", userDetails.getUsername());
        UserProfileResponse profile = userService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved successfully", profile));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "User Logout",
            description = "Invalidate and blacklist current JWT token in Redis"
    )
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok("Logout successful", null));
    }
}
