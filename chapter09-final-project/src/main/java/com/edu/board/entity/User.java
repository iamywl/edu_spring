package com.edu.board.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 사용자(회원) 엔티티
 *
 * Spring Security의 UserDetails를 구현하여
 * 인증/인가에 직접 사용할 수 있도록 합니다.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인에 사용되는 고유한 사용자명 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt로 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 사용자 권한 (USER 또는 ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role = Role.USER;

    /** 가입 일시 */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 사용자가 작성한 게시글 목록 */
    @OneToMany(mappedBy = "author")
    private List<Post> posts = new ArrayList<>();

    // === 기본 생성자 (JPA 전용) ===
    protected User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // === UserDetails 인터페이스 구현 ===

    /**
     * 사용자의 권한 목록을 반환합니다.
     * ROLE_ 접두사를 붙여 Spring Security 규칙을 따릅니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /** 계정 만료 여부 - 항상 유효 */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** 계정 잠금 여부 - 항상 잠금 해제 */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 자격 증명 만료 여부 - 항상 유효 */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** 계정 활성화 여부 - 항상 활성 */
    @Override
    public boolean isEnabled() {
        return true;
    }

    // === Getter / Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
