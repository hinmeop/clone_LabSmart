package com.dev.smartlabbackend.entity;

import com.dev.smartlabbackend.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bảng tasks — Task tối đa 3 cấp qua parent_task_id. Cột level do trigger trg_set_task_level tính, app không ghi.
 */
@Entity
@Table(name = "tasks", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    /** Do trigger DB set (1..3) */
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "level", insertable = false, updatable = false)
    private Short level;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    /** 0..100 */
    @Column(name = "progress", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progress = BigDecimal.ZERO;

    @Column(name = "result_file_url", columnDefinition = "text")
    private String resultFileUrl;

    @Column(name = "submit_note", columnDefinition = "text")
    private String submitNote;

    /** Bắt buộc khi status = REJECTED */
    @Column(name = "reviewer_comment", columnDefinition = "text")
    private String reviewerComment;

    /** Bắt buộc khi status = CANCEL */
    @Column(name = "cancel_reason", columnDefinition = "text")
    private String cancelReason;

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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "parentTask")
    @Builder.Default
    private List<Task> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskAssignee> assignees = new ArrayList<>();

    @OneToMany(mappedBy = "task")
    @Builder.Default
    private List<TaskComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task")
    @Builder.Default
    private List<TaskLog> logs = new ArrayList<>();

    @OneToMany(mappedBy = "task")
    @Builder.Default
    private List<TaskLogwork> logworks = new ArrayList<>();

    // ---- helper giữ đồng bộ 2 chiều cho collection cascade ----

    public void addAssignee(TaskAssignee assignee) {
        assignees.add(assignee);
        assignee.setTask(this);
    }

    public void removeAssignee(TaskAssignee assignee) {
        assignees.remove(assignee);
        assignee.setTask(null);
    }
}
