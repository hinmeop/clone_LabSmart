package com.dev.smartlabbackend.repository;

import com.dev.smartlabbackend.entity.News;
import com.dev.smartlabbackend.entity.enums.NewsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT n FROM News n WHERE n.deletedAt IS NULL " +
           "AND (:status IS NULL OR n.status = :status) " +
           "ORDER BY n.createdAt DESC")
    Page<News> findAllByFilters(@Param("status") NewsStatus status, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.status = :status " +
           "AND n.publishAt <= :now AND n.deletedAt IS NULL")
    List<News> findScheduledReadyToPublish(@Param("status") NewsStatus status,
                                           @Param("now") LocalDateTime now);
}
