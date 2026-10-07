package com.dev.smartlabbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a news article")
public class CreateNewsRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "News title", example = "Thông báo lịch bảo trì phòng lab", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Content is required")
    @Schema(description = "News content (HTML or plain text)", example = "<p>Nội dung thông báo...</p>", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "Cover image URL (uploaded by frontend)", example = "https://res.cloudinary.com/xxx/image/upload/cover.jpg")
    private String coverImageUrl;

    @Schema(description = "Pin this news to top", example = "false")
    private Boolean isPinned;
}
