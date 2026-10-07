package com.dev.smartlabbackend.controller;

import com.dev.smartlabbackend.common.response.ApiResponse;
import com.dev.smartlabbackend.dto.request.CreateProjectRequest;
import com.dev.smartlabbackend.dto.request.UpdateProjectRequest;
import com.dev.smartlabbackend.dto.response.ProjectResponse;
import com.dev.smartlabbackend.security.CustomUserDetails;
import com.dev.smartlabbackend.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "Endpoints for managing research projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(
            summary = "Create Project",
            description = "Business Admin/Manager creates a new research project. New project status is 'active'."
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(currentUser.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Project created successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "Get List Project",
            description = "Business Admin/Manager view list project"
    )
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getListProject(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        Page<ProjectResponse> responses = projectService.getListProject(page, size, search);
        return ResponseEntity.ok(ApiResponse.ok("Get list project successfully", responses));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Project",
            description = "Business Admin/Manager update project."
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request) {
        ProjectResponse response = projectService.updateProject(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("Updated project successfully", response));
    }

}
