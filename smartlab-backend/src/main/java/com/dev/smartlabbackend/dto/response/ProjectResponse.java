package com.dev.smartlabbackend.dto.response;

import com.dev.smartlabbackend.entity.enums.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse {
    Long id;
    String code;
    String name;
    String description;
    String domainName;
    String projectTypeName;
    String ownerName;
    ProjectStatus status;          // JSON: "pending" / "processing" / "done" / "cancel"
    BigDecimal progress;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;

    String cancelReason;
    Boolean isFeatured;

    String createdBy;
    String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}