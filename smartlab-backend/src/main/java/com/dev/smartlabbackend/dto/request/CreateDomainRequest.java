package com.dev.smartlabbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request body to create a domain")
public class CreateDomainRequest {

    @NotBlank(message = "Domain name is required")
    @Size(max = 255, message = "Domain name cannot exceed 255 characters")
    String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description;
}