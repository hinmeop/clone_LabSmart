package com.dev.smartlabbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body to update user profile")
public class UpdateProfileRequest {

    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    @Schema(description = "Full name", example = "Nguyen Van A")
    private String fullName;

    @PastOrPresent(message = "Date of birth must be in the past or today")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "2000-05-20")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Department cannot exceed 255 characters")
    @Schema(description = "Department name", example = "Software Engineering")
    private String department;

    @Size(max = 1000, message = "Note cannot exceed 1000 characters")
    @Schema(description = "Note", example = "Interested in AI and Cloud")
    private String note;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    @Schema(description = "Phone number", example = "0912345678")
    private String phone;
}
