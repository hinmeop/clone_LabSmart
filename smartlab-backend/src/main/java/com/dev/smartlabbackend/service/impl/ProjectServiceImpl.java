package com.dev.smartlabbackend.service.impl;

import com.dev.smartlabbackend.dto.request.CreateProjectRequest;
import com.dev.smartlabbackend.dto.request.UpdateProjectRequest;
import com.dev.smartlabbackend.dto.response.ProjectResponse;
import com.dev.smartlabbackend.entity.*;
import com.dev.smartlabbackend.entity.enums.CatalogStatus;
import com.dev.smartlabbackend.exception.AppException;
import com.dev.smartlabbackend.exception.BadRequestException;
import com.dev.smartlabbackend.exception.ConflictException;
import com.dev.smartlabbackend.exception.ResourceNotFoundException;
import com.dev.smartlabbackend.mapper.ProjectMapper;
import com.dev.smartlabbackend.repository.DomainRepository;
import com.dev.smartlabbackend.repository.ProjectRepository;
import com.dev.smartlabbackend.repository.ProjectTypeRepository;
import com.dev.smartlabbackend.repository.UserRepository;
import com.dev.smartlabbackend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final DomainRepository domainRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;


    @Override
    @Transactional
    public ProjectResponse createProject(Long currentUserId, CreateProjectRequest request) {

        //Người tạo – chỉ BA hoặc Manager (kiểm tra quyền trước)
        User createdBy = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        if (createdBy.getRole() != Role.BUSINESS_ADMIN && createdBy.getRole() != Role.MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only Business Admin or Manager can create project");
        }

        // Chuẩn hoá input
        request.setCode(request.getCode().trim());
        request.setName(request.getName().trim());

        //Check trùng code
        if (projectRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Project code already exists: " + request.getCode());
        }

        //Check trùng tên trong cùng 1 domain
        if (projectRepository.existsByDomainIdAndNameIgnoreCaseAndDeletedAtIsNull(
                request.getDomainId(), request.getName())) {
            throw new ConflictException("Project name already exists in this domain");
        }

        //Check ngày
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        //Check domain
        Domain domain = domainRepository.findByIdAndDeletedAtIsNull(request.getDomainId())
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + request.getDomainId()));

        if (domain.getStatus() != CatalogStatus.ACTIVE) {
            throw new BadRequestException("Domain is inactive");
        }

        //Check project type
        ProjectType projectType = projectTypeRepository.findByIdAndDeletedAtIsNull(request.getProjectTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectType not found with id: " + request.getProjectTypeId()));

        if (projectType.getStatus() != CatalogStatus.ACTIVE) {
            throw new BadRequestException("Project type is inactive");
        }

        // Request-> Entity(người tạo = owner)
        Project project = projectMapper.createProjectRequestToProject(request);
        project.setDomain(domain);
        project.setProjectType(projectType);
        project.setOwner(createdBy);
        project.setCreatedBy(createdBy);

        project = projectRepository.saveAndFlush(project);
        return projectMapper.toResponse(project);
    }

    @Transactional
    @Override
    public Page<ProjectResponse> getListProject(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projects = projectRepository.findAll(pageable);
        if (search != null && !search.isEmpty()) {
            projects = projectRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return projects.map(projectMapper::toResponse);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long currentUser, Long projectId, UpdateProjectRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User updateBy = userRepository.findByIdAndDeletedAtIsNull(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser));
        if (updateBy.getRole() != Role.MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only Manager can create project");
        }

        request.setCode(request.getCode().trim());
        request.setName(request.getName().trim());

        //Check trùng code
        if (projectRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Project code already exists: " + request.getCode());
        }


        Domain domain = domainRepository.findByIdAndDeletedAtIsNull(request.getDomainId())
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + request.getDomainId()));
        if (domain.getStatus() != CatalogStatus.ACTIVE) {
            throw new BadRequestException("Domain is inactive");
        }

        ProjectType projectType = projectTypeRepository.findByIdAndDeletedAtIsNull(request.getProjectTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectType not found with id: " + request.getProjectTypeId()));
        if (projectType.getStatus() != CatalogStatus.ACTIVE) {
            throw new BadRequestException("ProjectType is inactive");
        }

        if (projectRepository.existsByDomainIdAndNameIgnoreCaseAndDeletedAtIsNull(request.getDomainId(), request.getName())) {
            throw new ConflictException("Project name already exists in this domain");
        }
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        projectMapper.updateProjectRequestToProject(request, project);
        project.setDomain(domain);
        project.setProjectType(projectType);
        project.setUpdatedBy(updateBy);

        project = projectRepository.saveAndFlush(project);
        return projectMapper.toResponse(project);
    }
}
