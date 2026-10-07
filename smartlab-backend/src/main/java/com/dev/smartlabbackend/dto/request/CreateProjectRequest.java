package com.dev.smartlabbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

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

    @NotNull(message = "Owner is required")
    Long ownerId;

    LocalDate startDate;
    LocalDate endDate;
    Boolean isFeatured;
}
