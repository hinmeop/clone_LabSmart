package com.dev.smartlabbackend.entity;

import com.dev.smartlabbackend.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Bảng report_exports — Lịch sử xuất báo cáo (manual / AI).
 */
@Entity
@Table(name = "report_exports", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "semester", length = 10)
    private Semester semester;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "export_type", nullable = false, length = 10)
    @Builder.Default
    private ExportType exportType = ExportType.MANUAL;

    /** Bắt buộc khi exportType = AI */
    @Column(name = "ai_prompt", columnDefinition = "text")
    private String aiPrompt;

    @Column(name = "file_url", columnDefinition = "text")
    private String fileUrl;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ExportStatus status = ExportStatus.PROCESSING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}
