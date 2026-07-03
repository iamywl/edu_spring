package com.edu.board.service;

import com.edu.board.dto.CommentRequest;
import com.edu.board.dto.CommentResponse;
import com.edu.board.entity.Comment;
import com.edu.board.entity.Post;
import com.edu.board.entity.Role;
import com.edu.board.entity.User;
import com.edu.board.exception.ResourceNotFoundException;
import com.edu.board.exception.UnauthorizedException;
import com.edu.board.repository.CommentRepository;
import com.edu.board.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 서비스
 *
 * 댓글 작성, 조회, 삭제 비즈니스 로직을 처리합니다.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 댓글 응답 목록
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("게시글", postId);
        }

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    /**
     * 댓글을 작성합니다.
     *
     * @param postId  게시글 ID
     * @param request 댓글 작성 요청 DTO
     * @param author  작성자 (인증된 사용자)
     * @return 작성된 댓글 응답
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, User author) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글", postId));

        Comment comment = new Comment(request.content(), post, author);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    /**
     * 댓글을 삭제합니다. (작성자 또는 관리자만 가능)
     *
     * @param postId    게시글 ID
     * @param commentId 댓글 ID
     * @param user      삭제 요청자
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글", commentId));

        // 해당 게시글의 댓글이 맞는지 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw new ResourceNotFoundException("해당 게시글에 속한 댓글이 아닙니다.");
        }

        // 작성자이거나 관리자(ADMIN)만 삭제 가능
        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new UnauthorizedException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
