package com.dev.smartlabbackend.dto.response;

import com.dev.smartlabbackend.entity.enums.CatalogStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DomainResponse {
    Long id;
    String name;
    String description;
    CatalogStatus status;          // JSON: "active" / "inactive"
    Long createdById;
    String createdByName;

    Long updatedById;
    String updatedByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

}