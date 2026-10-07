package com.dev.smartlabbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request body to update a project")
public class UpdateProjectRequest {
    @NotBlank(message = "Project code is required")
    @Size(max = 50, message = "Project code cannot exceed 50 characters")
    String code;

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    String name;

    String description;

    @NotNull(message = "Domain is required")
    Long domainId;

    @NotNull(message = "Project type is required")
    Long projectTypeId;

    LocalDate startDate;
    LocalDate endDate;
    Boolean isFeatured;
}
