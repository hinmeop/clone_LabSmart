package com.dev.smartlabbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_email", nullable = false, unique = true, length = 255)
    private String orgEmail;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "department", length = 255)
    private String department;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "avatar_url", columnDefinition = "text")
    private String avatarUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // ===================== Quan hệ 1-N (phía ngược, mappedBy) =====================

    /** auth_verification_codes.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<AuthVerificationCode> verificationCodes = new ArrayList<>();

    /** system_settings.updated_by */
    @OneToMany(mappedBy = "updatedBy")
    @Builder.Default
    private List<SystemSetting> updatedSystemSettings = new ArrayList<>();

    /** notification_settings.updated_by */
    @OneToMany(mappedBy = "updatedBy")
    @Builder.Default
    private List<NotificationSetting> updatedNotificationSettings = new ArrayList<>();

    /** domains.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<Domain> createdDomains = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy")
    @Builder.Default
    private List<Domain> updatedDomains = new ArrayList<>();

    /** projects.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<Project> createdProjects = new ArrayList<>();

    /** projects.owner_id */
    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<Project> ownedProjects = new ArrayList<>();

    /** project_members.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ProjectMember> projectMemberships = new ArrayList<>();

    /** tasks.owner_id */
    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<Task> ownedTasks = new ArrayList<>();

    /** task_assignees.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<TaskAssignee> taskAssignments = new ArrayList<>();

    /** task_comments.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<TaskComment> taskComments = new ArrayList<>();

    /** task_logs.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<TaskLog> taskLogs = new ArrayList<>();

    /** task_logworks.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<TaskLogwork> taskLogworks = new ArrayList<>();

    /** news.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<News> createdNews = new ArrayList<>();

    /** posts.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<Post> createdPosts = new ArrayList<>();

    /** posts.reviewed_by */
    @OneToMany(mappedBy = "reviewedBy")
    @Builder.Default
    private List<Post> reviewedPosts = new ArrayList<>();

    /** post_comments.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<PostComment> postComments = new ArrayList<>();

    /** post_comments.deleted_by */
    @OneToMany(mappedBy = "deletedBy")
    @Builder.Default
    private List<PostComment> deletedPostComments = new ArrayList<>();

    /** recruitment_posts.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<RecruitmentPost> createdRecruitmentPosts = new ArrayList<>();

    /** applications.created_user_id */
    @OneToMany(mappedBy = "createdUser")
    @Builder.Default
    private List<Application> sourceApplications = new ArrayList<>();

    /** interviews.interviewer_id */
    @OneToMany(mappedBy = "interviewer")
    @Builder.Default
    private List<Interview> conductedInterviews = new ArrayList<>();

    /** folders.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<Folder> createdFolders = new ArrayList<>();

    /** materials.uploaded_by */
    @OneToMany(mappedBy = "uploadedBy")
    @Builder.Default
    private List<Material> uploadedMaterials = new ArrayList<>();

    /** notifications.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    /** report_exports.requested_by */
    @OneToMany(mappedBy = "requestedBy")
    @Builder.Default
    private List<ReportExport> reportExports = new ArrayList<>();

    /** chat_conversations.direct_user_low */
    @OneToMany(mappedBy = "directUserLow")
    @Builder.Default
    private List<ChatConversation> directConversationsAsLow = new ArrayList<>();

    /** chat_conversations.direct_user_high */
    @OneToMany(mappedBy = "directUserHigh")
    @Builder.Default
    private List<ChatConversation> directConversationsAsHigh = new ArrayList<>();

    /** chat_conversations.created_by */
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<ChatConversation> createdConversations = new ArrayList<>();

    /** chat_messages.sender_id */
    @OneToMany(mappedBy = "sender")
    @Builder.Default
    private List<ChatMessage> sentMessages = new ArrayList<>();

    /** chat_messages.deleted_by */
    @OneToMany(mappedBy = "deletedBy")
    @Builder.Default
    private List<ChatMessage> deletedMessages = new ArrayList<>();

    /** chat_participants.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ChatParticipant> chatParticipations = new ArrayList<>();

    /** chat_participants.added_by */
    @OneToMany(mappedBy = "addedBy")
    @Builder.Default
    private List<ChatParticipant> addedChatParticipants = new ArrayList<>();

    /** chat_message_reactions.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ChatMessageReaction> chatReactions = new ArrayList<>();

    /** chat_message_mentions.user_id */
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ChatMessageMention> chatMentions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status) && this.deletedAt == null && !Boolean.TRUE.equals(this.isDeleted);
    }
}
