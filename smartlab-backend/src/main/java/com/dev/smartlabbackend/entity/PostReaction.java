package com.dev.smartlabbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

/**
 * Bảng post_reactions — Cảm xúc / tương tác của người dùng trên bài viết.
 * Mỗi user tương tác 1 lần trên 1 post (uq_post_user_reaction).
 */
@Entity
@Table(name = "post_reactions", schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_post_user_reaction", columnNames = {"post_id", "user_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reaction_type", nullable = false, length = 32)
    @Builder.Default
    private String reactionType = "like";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
