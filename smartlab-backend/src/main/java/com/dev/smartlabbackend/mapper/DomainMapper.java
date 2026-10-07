package com.dev.smartlabbackend.mapper;

import com.dev.smartlabbackend.dto.request.CreateDomainRequest;
import com.dev.smartlabbackend.dto.request.UpdateDomainRequest;
import com.dev.smartlabbackend.dto.response.DomainResponse;
import com.dev.smartlabbackend.entity.Domain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DomainMapper {

    /**
     * Request -> Entity khi tạo mới.
     * id, createdAt, updatedAt do DB/Hibernate sinh; collection quan hệ không map từ request.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "recruitmentPosts", ignore = true)
    Domain toEntity(CreateDomainRequest request);

    /**
     * Entity -> Response trả về client.
     */
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    DomainResponse toResponse(Domain domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "recruitmentPosts", ignore = true)
    void updateEntity(UpdateDomainRequest request, @MappingTarget Domain domain);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    @Mapping(target = "updatedByName", source = "updatedBy.fullName")
    DomainResponse updateResponse(Domain domain);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    @Mapping(target = "updatedByName", source = "updatedBy.fullName")
    DomainResponse softDelete(Domain domain);

}
