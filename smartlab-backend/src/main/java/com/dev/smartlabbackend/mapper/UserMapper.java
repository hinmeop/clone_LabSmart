package com.dev.smartlabbackend.mapper;

import com.dev.smartlabbackend.dto.response.UserResponse;
import com.dev.smartlabbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", source = "orgEmail")
    @Mapping(target = "updatedBy", source = "updatedBy.fullName")
    UserResponse toResponse(User user);

    @Mapping(target = "email", source = "orgEmail")
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy.fullName")
    UserResponse softDeleteUser(User user);

}
