package com.edu.board.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 게시글 엔티티
 *
 * 사용자가 작성하는 게시글을 표현합니다.
 * 하나의 게시글은 하나의 작성자(User)와 여러 댓글(Comment)을 가집니다.
 */
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 제목 (최대 200자) */
    @Column(nullable = false, length = 200)
    private String title;

    /** 게시글 본문 (길이 제한 없음) */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 작성자 - 지연 로딩으로 필요할 때만 조회 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    /** 조회수 */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 댓글 목록
     * - cascade: 게시글 삭제 시 댓글도 함께 삭제
     * - orphanRemoval: 컬렉션에서 제거된 댓글은 DB에서도 삭제
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.BatchSize(size = 20)
    private List<Comment> comments = new ArrayList<>();

    /** 작성 일시 */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 수정 일시 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // === 기본 생성자 (JPA 전용) ===
    protected Post() {
    }

    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // === 비즈니스 메서드 ===

    /** 조회수를 1 증가시킵니다. */
    public void increaseViewCount() {
        this.viewCount++;
    }

    /** 게시글 내용을 수정하고 수정 일시를 갱신합니다. */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // === Getter / Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
