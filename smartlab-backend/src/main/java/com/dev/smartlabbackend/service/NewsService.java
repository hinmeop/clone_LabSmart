package com.dev.smartlabbackend.service;

import com.dev.smartlabbackend.dto.request.CreateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsStatusRequest;
import com.dev.smartlabbackend.dto.response.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface NewsService {

    NewsResponse createNews(CreateNewsRequest request, Long currentUserId);

    NewsResponse getNewsById(Long id);

    Page<NewsResponse> getAllNews(String status, int page, int size);

    NewsResponse updateNews(Long id, UpdateNewsRequest request);

    NewsResponse updateNewsStatus(Long id, UpdateNewsStatusRequest request);

    void deleteNews(Long id);
}
