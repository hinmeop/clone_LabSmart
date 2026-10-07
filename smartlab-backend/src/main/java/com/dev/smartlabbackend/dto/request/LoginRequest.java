package com.dev.smartlabbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for user login")
public class LoginRequest {

    @NotBlank(message = "Org email is required")
    @Email(message = "Org email must be a valid email address")
    @Schema(description = "Organization email of the user", example = "admin@smartlab.vn", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgEmail;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password of the account", example = "Admin@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
