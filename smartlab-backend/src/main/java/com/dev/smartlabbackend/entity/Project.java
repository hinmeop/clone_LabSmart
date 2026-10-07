package com.dev.smartlabbackend.entity;

import com.dev.smartlabbackend.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bảng projects — Dự án. Tên project unique trong cùng domain (partial index uq_projects_name_per_domain WHERE deleted_at IS NULL).
 */
@Entity
@Table(name = "projects", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_type_id", nullable = false)
    private ProjectType projectType;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PENDING;

    /** 0..100 */
    @Column(name = "progress", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progress = BigDecimal.ZERO;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** Bắt buộc khi status = CANCEL */
    @Column(name = "cancel_reason", columnDefinition = "text")
    private String cancelReason;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    /** posts.project_id */
    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    /** recruitment_posts.project_id */
    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<RecruitmentPost> recruitmentPosts = new ArrayList<>();

    /** folders.project_id */
    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<Folder> folders = new ArrayList<>();

    /** report_exports.project_id */
    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<ReportExport> reportExports = new ArrayList<>();

    /** chat_conversations.project_id */
    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<ChatConversation> chatConversations = new ArrayList<>();

    // ---- helper giữ đồng bộ 2 chiều cho collection cascade ----

    public void addMember(ProjectMember member) {
        members.add(member);
        member.setProject(this);
    }

    public void removeMember(ProjectMember member) {
        members.remove(member);
        member.setProject(null);
    }
}
