package com.dev.smartlabbackend.entity;

import com.dev.smartlabbackend.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bảng chat_messages — Tin nhắn. Thu hồi = soft delete (deleted_at + deleted_by). SYSTEM message có thể không có sender.
 */
@Entity
@Table(name = "chat_messages", schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_chat_msg_id_conv", columnNames = {"id", "conversation_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "message_type", nullable = false, length = 10)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    /** <= 10000 ký tự; bắt buộc với TEXT/SYSTEM */
    @Column(name = "content", columnDefinition = "text")
    private String content;

    /** FK kép (reply_to_message_id, conversation_id): chỉ reply tin trong cùng hội thoại */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private ChatMessage replyToMessage;

    /** Client sinh để chống gửi trùng khi retry */
    @Column(name = "client_message_id")
    private UUID clientMessageId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "active";

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessageAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessageReaction> reactions = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessageMention> mentions = new ArrayList<>();

    /** chat_conversations.last_message_id */
    @OneToMany(mappedBy = "lastMessage")
    @Builder.Default
    private List<ChatConversation> lastMessageOfConversations = new ArrayList<>();

    /** chat_messages.reply_to_message_id */
    @OneToMany(mappedBy = "replyToMessage")
    @Builder.Default
    private List<ChatMessage> replies = new ArrayList<>();

    /** chat_participants.last_read_message_id */
    @OneToMany(mappedBy = "lastReadMessage")
    @Builder.Default
    private List<ChatParticipant> lastReadByParticipants = new ArrayList<>();

    // ---- helper giữ đồng bộ 2 chiều cho collection cascade ----

    public void addAttachment(ChatMessageAttachment attachment) {
        attachments.add(attachment);
        attachment.setMessage(this);
    }

    public void removeAttachment(ChatMessageAttachment attachment) {
        attachments.remove(attachment);
        attachment.setMessage(null);
    }

    public void addReaction(ChatMessageReaction reaction) {
        reactions.add(reaction);
        reaction.setMessage(this);
    }

    public void removeReaction(ChatMessageReaction reaction) {
        reactions.remove(reaction);
        reaction.setMessage(null);
    }

    public void addMention(ChatMessageMention mention) {
        mentions.add(mention);
        mention.setMessage(this);
    }

    public void removeMention(ChatMessageMention mention) {
        mentions.remove(mention);
        mention.setMessage(null);
    }
}
