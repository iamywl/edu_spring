package com.edu.security.entity;

/**
 * 사용자 역할(Role) 열거형
 *
 * - USER: 일반 사용자 (기본 권한)
 * - ADMIN: 관리자 (모든 권한)
 *
 * Spring Security에서 hasRole("ADMIN") 사용 시
 * 내부적으로 "ROLE_ADMIN" 권한으로 매핑됩니다.
 */
public enum Role {
    USER,
    ADMIN
}
