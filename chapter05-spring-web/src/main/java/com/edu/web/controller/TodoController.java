package com.edu.web.controller;

import com.edu.web.dto.TodoRequest;
import com.edu.web.dto.TodoResponse;
import com.edu.web.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 할일 REST API 컨트롤러
 * HTTP 요청을 받아 서비스 계층에 위임하고, 적절한 HTTP 상태 코드와 함께 응답한다
 *
 * Swagger 애노테이션(@Tag, @Operation, @ApiResponse)을 추가하면
 * /swagger-ui.html 에서 API 문서를 보고 직접 테스트할 수 있다
 */
@Tag(name = "Todo API", description = "할일 관리 REST API")
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    // 생성자 주입 - 단일 생성자이므로 @Autowired 생략 가능
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 할일 목록 조회 및 검색
     * GET /api/todos                       → 전체 목록
     * GET /api/todos?completed=true        → 완료된 할일만
     * GET /api/todos?keyword=spring        → 제목에 "spring"이 포함된 할일만
     *
     * @RequestParam은 URL 쿼리 문자열(?key=value)의 값을 추출한다.
     * 경로 자체에서 값을 꺼내는 @PathVariable(/api/todos/{id})과 대조된다.
     * required = false로 두면 파라미터가 없어도 되며, 이 경우 해당 조건은 무시된다.
     */
    @Operation(summary = "할일 목록 조회/검색", description = "완료 여부(completed)와 키워드(keyword)로 필터링할 수 있다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(
            @Parameter(description = "완료 여부 필터 (true/false)")
            @RequestParam(required = false) Boolean completed,
            @Parameter(description = "제목 검색 키워드")
            @RequestParam(required = false) String keyword
    ) {
        List<TodoResponse> todos = todoService.search(completed, keyword);
        return ResponseEntity.ok(todos);
    }

    /**
     * ID로 할일 단건 조회
     * GET /api/todos/{id} → 200 OK / 404 Not Found
     */
    @Operation(summary = "할일 단건 조회", description = "ID로 특정 할일을 조회한다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 할일이 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(
            @Parameter(description = "할일 ID") @PathVariable Long id) {
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    /**
     * 새로운 할일 생성
     * POST /api/todos → 201 Created / 400 Bad Request (유효성 검증 실패)
     * @Valid 애노테이션으로 TodoRequest의 검증 규칙을 적용한다
     */
    @Operation(summary = "할일 생성", description = "새로운 할일을 등록한다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        TodoResponse created = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 기존 할일 수정
     * PUT /api/todos/{id} → 200 OK / 404 Not Found
     */
    @Operation(summary = "할일 수정", description = "ID로 기존 할일을 수정한다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 할일이 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @Parameter(description = "할일 ID") @PathVariable Long id,
            @Valid @RequestBody TodoRequest request
    ) {
        TodoResponse updated = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 할일 삭제
     * DELETE /api/todos/{id} → 204 No Content / 404 Not Found
     */
    @Operation(summary = "할일 삭제", description = "ID로 할일을 삭제한다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 할일이 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "할일 ID") @PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
