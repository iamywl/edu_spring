package com.edu.web.service;

import com.edu.web.dto.TodoRequest;
import com.edu.web.dto.TodoResponse;
import com.edu.web.exception.TodoNotFoundException;
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

    // 스레드 안전한 인메모리 저장소
    private final Map<Long, TodoData> store = new ConcurrentHashMap<>();

    // 스레드 안전한 ID 생성기
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 전체 할일 목록을 조회한다
     */
    public List<TodoResponse> getAllTodos() {
        return store.values().stream()
                .map(this::toResponse)
                .toList();
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
