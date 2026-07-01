package com.edu.board.service;

import com.edu.board.dto.*;
import com.edu.board.entity.Post;
import com.edu.board.entity.Role;
import com.edu.board.entity.User;
import com.edu.board.exception.ResourceNotFoundException;
import com.edu.board.exception.UnauthorizedException;
import com.edu.board.repository.CommentRepository;
import com.edu.board.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 게시글 서비스
 *
 * 게시글의 CRUD, 검색, 페이징, 조회수 증가 등
 * 핵심 비즈니스 로직을 처리합니다.
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 게시글 목록을 페이징하여 조회합니다.
     * 키워드가 있으면 제목 검색, 없으면 전체 조회합니다.
     * 최신 글이 먼저 오도록 createdAt 내림차순 정렬합니다.
     *
     * @param page    페이지 번호 (0부터 시작)
     * @param size    페이지 크기
     * @param keyword 검색 키워드 (null이면 전체 조회)
     * @return 페이징된 게시글 목록 응답
     */
    @Transactional(readOnly = true)
    public PageResponse<PostListResponse> getPosts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Post> postPage;
        if (keyword != null && !keyword.isBlank()) {
            // 키워드가 있으면 제목 검색 (author는 @EntityGraph로 함께 로딩됨)
            postPage = postRepository.findByTitleContaining(keyword, pageable);
        } else {
            // 키워드가 없으면 전체 조회 (author는 @EntityGraph로 함께 로딩됨)
            postPage = postRepository.findAll(pageable);
        }

        // === N+1 방지: 현재 페이지 게시글들의 댓글 수를 COUNT 쿼리 한 번으로 집계 ===
        // 게시글마다 post.getComments().size()를 호출하면 N번의 추가 쿼리가 나가므로,
        // postId 목록을 모아 GROUP BY 집계로 한 번에 조회한 뒤 Map으로 만들어 사용한다.
        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();
        Map<Long, Long> commentCounts = postIds.isEmpty()
                ? Map.of()
                : commentRepository.countByPostIds(postIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],   // postId
                        row -> (Long) row[1])); // 댓글 수

        // Post 엔티티 -> PostListResponse DTO 변환 (댓글 수는 미리 집계한 값을 사용)
        List<PostListResponse> content = postPage.getContent().stream()
                .map(post -> PostListResponse.from(
                        post,
                        commentCounts.getOrDefault(post.getId(), 0L)))
                .toList();

        return PageResponse.from(postPage, content);
    }

    /**
     * 게시글 상세 조회
     * 조회 시 조회수를 1 증가시킵니다.
     *
     * @param id 게시글 ID
     * @return 게시글 상세 응답
     */
    @Transactional
    public PostResponse getPost(Long id) {
        Post post = findPostById(id);
        post.increaseViewCount(); // 조회수 증가
        return PostResponse.from(post);
    }

    /**
     * 게시글 작성
     *
     * @param request 게시글 작성 요청 DTO
     * @param author  작성자 (인증된 사용자)
     * @return 작성된 게시글 응답
     */
    @Transactional
    public PostResponse createPost(PostRequest request, User author) {
        Post post = new Post(request.title(), request.content(), author);
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    /**
     * 게시글 수정 (작성자만 가능)
     *
     * @param id      게시글 ID
     * @param request 수정 요청 DTO
     * @param user    수정 요청자 (작성자 여부 확인)
     * @return 수정된 게시글 응답
     */
    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, User user) {
        Post post = findPostById(id);

        // 작성자 본인만 수정 가능
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedException("게시글 수정 권한이 없습니다.");
        }

        post.update(request.title(), request.content());
        return PostResponse.from(post);
    }

    /**
     * 게시글 삭제 (작성자 또는 관리자만 가능)
     *
     * @param id   게시글 ID
     * @param user 삭제 요청자
     */
    @Transactional
    public void deletePost(Long id, User user) {
        Post post = findPostById(id);

        // 작성자이거나 관리자(ADMIN)만 삭제 가능
        boolean isAuthor = post.getAuthor().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new UnauthorizedException("게시글 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 게시글을 ID로 조회하는 내부 메서드
     * 존재하지 않으면 ResourceNotFoundException 발생
     */
    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글", id));
    }
}
