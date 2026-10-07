package com.dev.smartlabbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating a news article")
public class UpdateNewsRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "News title", example = "Thông báo cập nhật lịch bảo trì")
    private String title;

    @Schema(description = "News content (HTML or plain text)", example = "<p>Nội dung cập nhật...</p>")
    private String content;

    @Schema(description = "Cover image URL", example = "https://res.cloudinary.com/xxx/image/upload/new-cover.jpg")
    private String coverImageUrl;

    @Schema(description = "Pin this news to top", example = "true")
    private Boolean isPinned;
}
