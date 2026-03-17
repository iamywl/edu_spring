package com.edu.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 사용자 엔티티
 *
 * UserDetails 인터페이스를 구현하여 Spring Security와 통합합니다.
 * - getAuthorities(): 사용자의 권한 목록 반환 (ROLE_ 접두어 필수)
 * - getUsername(): 인증에 사용할 사용자 식별자
 * - getPassword(): 암호화된 비밀번호
 * - isAccountNonExpired() 등: 계정 상태 확인 메서드 (기본 true)
 */
@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 사용자 아이디 (유니크) */
    @Column(nullable = false, unique = true)
    private String username;

    /** BCrypt로 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 사용자 역할 (USER 또는 ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ======== UserDetails 인터페이스 구현 ========

    /**
     * 사용자의 권한 목록을 반환합니다.
     * Spring Security에서 hasRole("ADMIN")은 "ROLE_ADMIN" 권한을 확인하므로
     * 반드시 "ROLE_" 접두어를 붙여야 합니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /** 계정 만료 여부 (true = 만료되지 않음) */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** 계정 잠금 여부 (true = 잠기지 않음) */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 자격 증명 만료 여부 (true = 만료되지 않음) */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** 계정 활성화 여부 (true = 활성화) */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
