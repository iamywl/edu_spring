package com.edu.web.controller;

import com.edu.web.dto.TodoRequest;
import com.edu.web.dto.TodoResponse;
import com.edu.web.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 할일 REST API 컨트롤러
 * HTTP 요청을 받아 서비스 계층에 위임하고, 적절한 HTTP 상태 코드와 함께 응답한다
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    // 생성자 주입 - 단일 생성자이므로 @Autowired 생략 가능
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 전체 할일 목록 조회
     * GET /api/todos → 200 OK
     */
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * ID로 할일 단건 조회
     * GET /api/todos/{id} → 200 OK / 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    /**
     * 새로운 할일 생성
     * POST /api/todos → 201 Created / 400 Bad Request (유효성 검증 실패)
     * @Valid 애노테이션으로 TodoRequest의 검증 규칙을 적용한다
     */
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        TodoResponse created = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 기존 할일 수정
     * PUT /api/todos/{id} → 200 OK / 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request
    ) {
        TodoResponse updated = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 할일 삭제
     * DELETE /api/todos/{id} → 204 No Content / 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
