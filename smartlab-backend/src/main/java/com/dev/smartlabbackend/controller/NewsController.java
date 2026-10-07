package com.dev.smartlabbackend.controller;

import com.dev.smartlabbackend.common.response.ApiResponse;
import com.dev.smartlabbackend.dto.request.CreateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsStatusRequest;
import com.dev.smartlabbackend.dto.response.NewsResponse;
import com.dev.smartlabbackend.security.CustomUserDetails;
import com.dev.smartlabbackend.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "Endpoints for managing news articles")
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    @Operation(summary = "Create News", description = "Create a new news article with status = draft")
    public ResponseEntity<ApiResponse<NewsResponse>> createNews(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateNewsRequest request) {
        NewsResponse response = newsService.createNews(request, currentUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("News created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get News By ID", description = "Get a single news article by its ID")
    public ResponseEntity<ApiResponse<NewsResponse>> getNewsById(@PathVariable Long id) {
        NewsResponse response = newsService.getNewsById(id);
        return ResponseEntity.ok(ApiResponse.ok("News retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get List News", description = "Get paginated list of news, optionally filtered by status")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getAllNews(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsResponse> response = newsService.getAllNews(status, page, size);
        return ResponseEntity.ok(ApiResponse.ok("News list retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update News", description = "Update news title, content, cover image, or pin status")
    public ResponseEntity<ApiResponse<NewsResponse>> updateNews(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNewsRequest request) {
        NewsResponse response = newsService.updateNews(id, request);
        return ResponseEntity.ok(ApiResponse.ok("News updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update News Status", description = "Change news status (draft, scheduled, published, hidden). Set publishAt to schedule.")
    public ResponseEntity<ApiResponse<NewsResponse>> updateNewsStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNewsStatusRequest request) {
        NewsResponse response = newsService.updateNewsStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("News status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete News", description = "Soft delete a news article")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok(ApiResponse.ok("News deleted successfully", null));
    }
}
