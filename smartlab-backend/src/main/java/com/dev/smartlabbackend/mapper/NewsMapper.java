package com.dev.smartlabbackend.mapper;

import com.dev.smartlabbackend.dto.request.CreateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsRequest;
import com.dev.smartlabbackend.dto.response.NewsResponse;
import com.dev.smartlabbackend.entity.News;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    /**
     * Request -> Entity khi tạo bài viết mới.
     * Mặc định status = DRAFT, các trường id/timestamps/createdBy do hệ thống gán.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "publishAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isPinned", defaultValue = "false")
    News toEntity(CreateNewsRequest request);

    /**
     * Entity -> Response trả về client, map thông tin tác giả từ quan hệ createdBy.
     */
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    NewsResponse toResponse(News news);

    /**
     * Cập nhật thông tin bài viết từ request (bỏ qua các field null).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(UpdateNewsRequest request, @MappingTarget News news);
}
