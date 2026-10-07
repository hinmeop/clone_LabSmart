package com.dev.smartlabbackend.mapper;

import com.dev.smartlabbackend.dto.request.CreateProjectRequest;
import com.dev.smartlabbackend.dto.request.UpdateProjectRequest;
import com.dev.smartlabbackend.dto.response.ProjectResponse;
import com.dev.smartlabbackend.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "domain.id", source = "domainId")
    @Mapping(target = "projectType.id", source = "projectTypeId")
    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "progress", constant = "0")
    Project createProjectRequestToProject(CreateProjectRequest request);

    @Mapping(target = "domainName", source = "domain.name")
    @Mapping(target = "projectTypeName", source = "projectType.name")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "updatedBy", source = "updatedBy.fullName")
    @Mapping(target = "createdBy", source = "createdBy.fullName")
    ProjectResponse toResponse(Project project);

    /*
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Dòng này quy định cách xử lý khi một field trong request là null. Nếu không có nó, MapStruct sinh ra code gần như sau:

    project.setName(request.getName());
    project.setDescription(request.getDescription()); // FE không gửi → null → xoá mất description cũ

    Khi có  @BeanMapping, code sinh ra sẽ thành:
    if (request.getDescription() != null) {
    project.setDescription(request.getDescription());
    }
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "domain", ignore = true)
    @Mapping(target = "projectType", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void  updateProjectRequestToProject(UpdateProjectRequest request, @MappingTarget Project project);

}
