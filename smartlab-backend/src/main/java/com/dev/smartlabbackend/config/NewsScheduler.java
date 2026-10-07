package com.dev.smartlabbackend.config;

import com.dev.smartlabbackend.entity.News;
import com.dev.smartlabbackend.entity.enums.NewsStatus;
import com.dev.smartlabbackend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler tự động chuyển news từ SCHEDULED → PUBLISHED khi đến giờ publish_at.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final NewsRepository newsRepository;

    /**
     * Chạy mỗi phút, kiểm tra news đã đến giờ publish.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void publishScheduledNews() {
        LocalDateTime now = LocalDateTime.now();
        List<News> readyNews = newsRepository.findScheduledReadyToPublish(NewsStatus.SCHEDULED, now);

        if (!readyNews.isEmpty()) {
            for (News news : readyNews) {
                news.setStatus(NewsStatus.PUBLISHED);
                log.info("Auto-published scheduled news id {} (publishAt: {})", news.getId(), news.getPublishAt());
            }
            newsRepository.saveAll(readyNews);
            log.info("Published {} scheduled news articles", readyNews.size());
        }
    }
}
