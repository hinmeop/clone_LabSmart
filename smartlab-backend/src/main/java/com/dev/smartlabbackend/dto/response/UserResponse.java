package com.dev.smartlabbackend.dto.response;

import com.dev.smartlabbackend.entity.Role;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    String email;
    String fullName;
    String department;
    String note;
    Role role;
    UserStatus status;
    String avatarUrl;
    String phone;
    String  updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;
    Boolean isDeleted;
}
