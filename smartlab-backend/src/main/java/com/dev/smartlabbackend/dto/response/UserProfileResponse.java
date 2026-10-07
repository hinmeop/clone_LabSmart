package com.dev.smartlabbackend.dto.response;

import com.dev.smartlabbackend.entity.Role;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response details")
public class UserProfileResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Organization Email", example = "admin@smartlab.vn")
    private String orgEmail;

    @Schema(description = "Full name", example = "System Administrator")
    private String fullName;

    @Schema(description = "Date of birth", example = "2000-01-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Schema(description = "Department name", example = "IT Department")
    private String department;

    @Schema(description = "Additional note", example = "Lead DevOps and System Admin")
    private String note;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "Phone number", example = "0987654321")
    private String phone;

    @Schema(description = "Role in system (it_admin, business_admin, manager, leader, member, guest)", example = "it_admin")
    private Role role;

    @Schema(description = "Account status (active, inactive, locked)", example = "active")
    private UserStatus status;

    @Schema(description = "Created timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last updated timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserProfileResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserProfileResponse.builder()
                .id(user.getId())
                .orgEmail(user.getOrgEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .department(user.getDepartment())
                .note(user.getNote())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
