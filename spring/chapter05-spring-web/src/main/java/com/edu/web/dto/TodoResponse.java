package com.edu.web.dto;

import java.time.LocalDateTime;

/**
 * 할일 응답 DTO
 * 클라이언트에게 반환할 데이터를 담는다
 */
public record TodoResponse(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}
