package com.dev.smartlabbackend.controller;

import com.dev.smartlabbackend.common.response.ApiResponse;
import com.dev.smartlabbackend.dto.request.CreateDomainRequest;
import com.dev.smartlabbackend.dto.request.UpdateDomainRequest;
import com.dev.smartlabbackend.dto.response.DomainResponse;
import com.dev.smartlabbackend.security.CustomUserDetails;
import com.dev.smartlabbackend.service.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/domains")
@RequiredArgsConstructor
@Tag(name = "Domain", description = "Endpoints for managing research domains")
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    @Operation(
            summary = "Create Domain",
            description = "Business Admin creates a new research domain. New domain status is 'active'."
    )
    public ResponseEntity<ApiResponse<DomainResponse>> createDomain(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateDomainRequest request) {
        DomainResponse response = domainService.createDomain(currentUser.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Domain created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Domain",
            description = "Business Admin/Manager update domain."
    )
    public ResponseEntity<ApiResponse<DomainResponse>> updateDomain(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateDomainRequest request) {
        DomainResponse response = domainService.updateDomain(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("Domain updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft Delete Domain",
            description = "Business Admin/Manager soft delete domain."
    )
    public ResponseEntity<ApiResponse<DomainResponse>> softDeleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        DomainResponse response = domainService.softDeleteDomain(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Domain soft deleted successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "Get List Domain",
            description = "Business Admin/Manager view list domain."
    )
    public ResponseEntity<ApiResponse<Page<DomainResponse>>> getListDomain(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        Page<DomainResponse> result = domainService.getListDomain(page, size, search);
        return ResponseEntity.ok(ApiResponse.ok("Get list domain successfully", result));
    }
}
