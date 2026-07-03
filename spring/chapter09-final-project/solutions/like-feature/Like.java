package com.edu.board.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 좋아요(Like) 엔티티 — [도전 과제 참고 답안]
 *
 * "어떤 사용자(User)가 어떤 게시글(Post)에 좋아요를 눌렀다"는 사실을 저장합니다.
 * - User (1) ─ (N) Like
 * - Post (1) ─ (N) Like
 *
 * 핵심 설계 포인트: (user_id, post_id) 복합 유니크 제약
 * → 같은 사용자가 같은 게시글에 두 번 좋아요를 누르는 것을 DB 레벨에서 막습니다.
 *   (애플리케이션 검증을 우회하는 동시 요청까지 막아주는 최후의 방어선)
 *
 * 주의: "like"는 SQL 예약어이므로 테이블명을 "post_like"로 지정합니다.
 */
@Entity
@Table(
        name = "post_like",
        uniqueConstraints = {
                // 한 사용자는 한 게시글에 좋아요를 한 번만 누를 수 있다 (중복 방지)
                @UniqueConstraint(name = "uk_like_user_post", columnNames = {"user_id", "post_id"})
        }
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 좋아요를 누른 사용자 - 지연 로딩 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 좋아요가 눌린 게시글 - 지연 로딩 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** 좋아요 누른 일시 */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // === 기본 생성자 (JPA 전용) ===
    protected Like() {
    }

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    // === Getter ===

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
