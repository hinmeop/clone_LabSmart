package com.dev.smartlabbackend.dto.response;

import com.dev.smartlabbackend.entity.enums.NewsStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "News article response")
public class NewsResponse {

    @Schema(description = "News ID", example = "1")
    private Long id;

    @Schema(description = "News title", example = "Thông báo lịch bảo trì phòng lab")
    private String title;

    @Schema(description = "News content", example = "<p>Nội dung thông báo...</p>")
    private String content;

    @Schema(description = "Cover image URL")
    private String coverImageUrl;

    @Schema(description = "News status", example = "published")
    private NewsStatus status;

    @Schema(description = "Pinned to top", example = "false")
    private Boolean isPinned;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Scheduled publish time")
    private LocalDateTime publishAt;

    @Schema(description = "Author ID", example = "1")
    private Long createdById;

    @Schema(description = "Author full name", example = "System Administrator")
    private String createdByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;
}
