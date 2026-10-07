package com.dev.smartlabbackend.entity;

import com.dev.smartlabbackend.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bảng chat_conversations — Cuộc trò chuyện. DIRECT: direct_user_low < direct_user_high, name/project null. GROUP: name bắt buộc.
 * Chat không có trigger updated_at -> @UpdateTimestamp do app set.
 */
@Entity
@Table(name = "chat_conversations", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_type", nullable = false, length = 10)
    private ConversationType conversationType;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "avatar_url", columnDefinition = "text")
    private String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direct_user_low")
    private User directUserLow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direct_user_high")
    private User directUserHigh;

    /** DB: ON DELETE SET NULL */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    /** FK kép (last_message_id, id) -> chat_messages(id, conversation_id): tin phải thuộc chính hội thoại này */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private ChatMessage lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "active";

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

    @OneToMany(mappedBy = "conversation")
    @Builder.Default
    private List<ChatParticipant> participants = new ArrayList<>();

    /** chat_messages.conversation_id */
    @OneToMany(mappedBy = "conversation")
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();
}
