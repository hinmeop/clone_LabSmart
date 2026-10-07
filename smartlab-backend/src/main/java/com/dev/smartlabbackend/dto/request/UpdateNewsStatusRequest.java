package com.dev.smartlabbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating news status")
public class UpdateNewsStatusRequest {

    @NotBlank(message = "Status is required")
    @Schema(description = "New status: draft, scheduled, published, hidden", example = "published", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Scheduled publish time (required when status = scheduled)", example = "2026-10-10 08:00:00")
    private LocalDateTime publishAt;
}
