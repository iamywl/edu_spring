package com.edu.web.service;

import com.edu.web.dto.TodoRequest;
import com.edu.web.dto.TodoResponse;
import com.edu.web.exception.TodoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 할일 비즈니스 로직을 담당하는 서비스
 * 데이터베이스 대신 ConcurrentHashMap을 사용하여 인메모리 저장소로 동작한다
 */
@Service
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    // 스레드 안전한 인메모리 저장소
    private final Map<Long, TodoData> store = new ConcurrentHashMap<>();

    // 스레드 안전한 ID 생성기
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 전체 할일 목록을 조회한다
     */
    public List<TodoResponse> getAllTodos() {
        log.debug("전체 할일 목록 조회 - 현재 저장된 개수: {}", store.size());
        return store.values().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 조건에 맞는 할일을 검색한다
     * - completed: 완료 여부로 필터링 (null이면 필터링하지 않음)
     * - keyword:   제목에 키워드가 포함된 항목만 필터링 (null/빈 문자열이면 필터링하지 않음)
     * 두 조건 모두 null이면 전체 목록을 반환한다
     */
    public List<TodoResponse> search(Boolean completed, String keyword) {
        log.debug("할일 검색 - completed={}, keyword={}", completed, keyword);
        List<TodoResponse> result = store.values().stream()
                // completed가 지정된 경우에만 완료 여부로 필터링
                .filter(data -> completed == null || data.completed() == completed)
                // keyword가 지정된 경우에만 제목 포함 여부로 필터링 (대소문자 무시)
                .filter(data -> keyword == null || keyword.isBlank()
                        || data.title().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::toResponse)
                .toList();
        log.debug("검색 결과 개수: {}", result.size());
        return result;
    }

    /**
     * ID로 할일을 조회한다
     * 존재하지 않으면 TodoNotFoundException을 던진다
     */
    public TodoResponse getTodoById(Long id) {
        TodoData data = store.get(id);
        if (data == null) {
            throw new TodoNotFoundException(id);
        }
        return toResponse(data);
    }

    /**
     * 새로운 할일을 생성한다
     * AtomicLong으로 유일한 ID를 생성하여 할당한다
     */
    public TodoResponse createTodo(TodoRequest request) {
        Long id = idGenerator.getAndIncrement();
        TodoData data = new TodoData(
                id,
                request.title(),
                request.description(),
                request.completed(),
                LocalDateTime.now()
        );
        store.put(id, data);
        log.debug("할일 생성 완료 - id={}, title={}", id, request.title());
        return toResponse(data);
    }

    /**
     * 기존 할일을 수정한다
     * 존재하지 않으면 TodoNotFoundException을 던진다
     */
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        TodoData existing = store.get(id);
        if (existing == null) {
            throw new TodoNotFoundException(id);
        }
        // 기존 생성 시각은 유지하고 나머지 필드를 업데이트한다
        TodoData updated = new TodoData(
                id,
                request.title(),
                request.description(),
                request.completed(),
                existing.createdAt()
        );
        store.put(id, updated);
        return toResponse(updated);
    }

    /**
     * 할일을 삭제한다
     * 존재하지 않으면 TodoNotFoundException을 던진다
     */
    public void deleteTodo(Long id) {
        if (store.remove(id) == null) {
            throw new TodoNotFoundException(id);
        }
    }

    /**
     * 내부 데이터를 응답 DTO로 변환한다
     */
    private TodoResponse toResponse(TodoData data) {
        return new TodoResponse(
                data.id(),
                data.title(),
                data.description(),
                data.completed(),
                data.createdAt()
        );
    }

    /**
     * 인메모리 저장소에 보관할 내부 데이터 모델
     * 외부에 노출되지 않는 private record이다
     */
    private record TodoData(
            Long id,
            String title,
            String description,
            boolean completed,
            LocalDateTime createdAt
    ) {}
}
