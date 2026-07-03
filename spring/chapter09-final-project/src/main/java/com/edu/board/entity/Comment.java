package com.edu.board.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 댓글 엔티티
 *
 * 게시글에 달리는 댓글을 표현합니다.
 * 하나의 댓글은 하나의 게시글(Post)과 하나의 작성자(User)에 속합니다.
 */
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 댓글 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 소속 게시글 - 지연 로딩 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /** 작성자 - 지연 로딩 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    /** 작성 일시 */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // === 기본 생성자 (JPA 전용) ===
    protected Comment() {
    }

    public Comment(String content, Post post, User author) {
        this.content = content;
        this.post = post;
        this.author = author;
    }

    // === Getter / Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
