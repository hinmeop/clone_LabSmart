package com.dev.smartlabbackend.service.impl;

import com.dev.smartlabbackend.dto.request.CreateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsRequest;
import com.dev.smartlabbackend.dto.request.UpdateNewsStatusRequest;
import com.dev.smartlabbackend.dto.response.NewsResponse;
import com.dev.smartlabbackend.entity.News;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.enums.NewsStatus;
import com.dev.smartlabbackend.exception.BadRequestException;
import com.dev.smartlabbackend.exception.ResourceNotFoundException;
import com.dev.smartlabbackend.mapper.NewsMapper;
import com.dev.smartlabbackend.repository.NewsRepository;
import com.dev.smartlabbackend.repository.UserRepository;
import com.dev.smartlabbackend.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper newsMapper;

    @Override
    @Transactional
    public NewsResponse createNews(CreateNewsRequest request, Long currentUserId) {
        User author = userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        News news = newsMapper.toEntity(request);
        news.setCreatedBy(author);

        news = newsRepository.save(news);
        log.info("News created with id {} by user {}", news.getId(), currentUserId);
        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponse getNewsById(Long id) {
        News news = findActiveNewsById(id);
        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponse> getAllNews(String status, int page, int size) {
        NewsStatus newsStatus = null;
        if (status != null && !status.isBlank()) {
            newsStatus = NewsStatus.fromValue(status);
        }

        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findAllByFilters(newsStatus, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    @Transactional
    public NewsResponse updateNews(Long id, UpdateNewsRequest request) {
        News news = findActiveNewsById(id);
        newsMapper.updateEntity(request, news);

        news = newsRepository.save(news);
        log.info("News updated with id {}", id);
        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional
    public NewsResponse updateNewsStatus(Long id, UpdateNewsStatusRequest request) {
        News news = findActiveNewsById(id);

        // Nếu có publishAt → tự động chuyển sang scheduled
        if (request.getPublishAt() != null) {
            if (request.getPublishAt().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Publish time must be in the future");
            }
            news.setPublishAt(request.getPublishAt());
            news.setStatus(NewsStatus.SCHEDULED);
            log.info("News {} scheduled to publish at {}", id, request.getPublishAt());
        } else {
            NewsStatus newStatus = NewsStatus.fromValue(request.getStatus());

            if (newStatus == NewsStatus.SCHEDULED) {
                throw new BadRequestException("publishAt is required when setting status to scheduled");
            }

            news.setStatus(newStatus);

            // Nếu publish ngay → set publishAt = now
            if (newStatus == NewsStatus.PUBLISHED && news.getPublishAt() == null) {
                news.setPublishAt(LocalDateTime.now());
            }

            log.info("News {} status changed to {}", id, newStatus.getValue());
        }

        news = newsRepository.save(news);
        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        News news = findActiveNewsById(id);
        news.setDeletedAt(LocalDateTime.now());
        newsRepository.save(news);
        log.info("News soft-deleted with id {}", id);
    }

    private News findActiveNewsById(Long id) {
        return newsRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
    }
}
