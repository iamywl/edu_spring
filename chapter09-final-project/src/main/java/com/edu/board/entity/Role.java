package com.edu.board.entity;

/**
 * 사용자 권한(역할)을 정의하는 열거형
 * - USER: 일반 사용자
 * - ADMIN: 관리자 (다른 사용자의 게시글/댓글 삭제 가능)
 */
public enum Role {
    USER,
    ADMIN
}
