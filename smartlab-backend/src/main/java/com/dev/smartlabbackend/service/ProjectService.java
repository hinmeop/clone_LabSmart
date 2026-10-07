package com.dev.smartlabbackend.service;

import com.dev.smartlabbackend.dto.request.CreateProjectRequest;
import com.dev.smartlabbackend.dto.request.UpdateProjectRequest;
import com.dev.smartlabbackend.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;

public interface ProjectService {
    ProjectResponse createProject(Long currentUser, CreateProjectRequest request);

    Page<ProjectResponse> getListProject(int page, int size, String search);

    ProjectResponse updateProject(Long currentUser, Long projectId, UpdateProjectRequest request);
}
