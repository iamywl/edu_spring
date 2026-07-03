package com.edu.board.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 응답 래퍼 DTO (제네릭)
 *
 * Spring Data의 Page 객체를 API 응답용으로 변환합니다.
 *
 * @param content       현재 페이지의 데이터 목록
 * @param page          현재 페이지 번호 (0부터 시작)
 * @param size          페이지 크기
 * @param totalElements 전체 데이터 수
 * @param totalPages    전체 페이지 수
 * @param first         첫 페이지 여부
 * @param last          마지막 페이지 여부
 * @param <T>           응답 데이터 타입
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    /** Spring Data Page 객체를 PageResponse로 변환하는 팩토리 메서드 */
    public static <T> PageResponse<T> from(Page<?> pageData, List<T> content) {
        return new PageResponse<>(
                content,
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isFirst(),
                pageData.isLast()
        );
    }
}
