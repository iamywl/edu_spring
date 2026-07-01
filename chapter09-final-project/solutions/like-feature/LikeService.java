package com.edu.board.service;

import com.edu.board.entity.Like;
import com.edu.board.entity.Post;
import com.edu.board.entity.User;
import com.edu.board.exception.ResourceNotFoundException;
import com.edu.board.repository.LikeRepository;
import com.edu.board.repository.PostRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좋아요 서비스 — [도전 과제 참고 답안]
 *
 * 좋아요 추가/취소(토글)와 개수 조회 비즈니스 로직을 담당합니다.
 * PostService / CommentService와 동일한 패턴(생성자 주입, @Transactional)을 따릅니다.
 */
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    /**
     * 좋아요 추가 (POST /api/posts/{id}/likes)
     *
     * 이미 좋아요를 누른 상태라면 중복 저장하지 않고 그냥 넘어갑니다(멱등).
     * 또한 (user, post) 복합 유니크 제약 덕분에 동시 요청에서도 중복이 막힙니다.
     *
     * @param postId 게시글 ID
     * @param user   인증된 사용자
     * @return 처리 후 해당 게시글의 좋아요 총 개수
     */
    @Transactional
    public long like(Long postId, User user) {
        Post post = findPostById(postId);

        // 애플리케이션 레벨 중복 체크 (1차 방어)
        if (!likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            try {
                likeRepository.save(new Like(user, post));
            } catch (DataIntegrityViolationException e) {
                // 동시 요청으로 유니크 제약 위반이 발생해도 무시 (2차 방어 / 멱등 보장)
            }
        }

        return likeRepository.countByPostId(postId);
    }

    /**
     * 좋아요 취소 (DELETE /api/posts/{id}/likes)
     *
     * 누른 적이 없으면 아무 일도 하지 않습니다(멱등).
     *
     * @param postId 게시글 ID
     * @param user   인증된 사용자
     * @return 처리 후 해당 게시글의 좋아요 총 개수
     */
    @Transactional
    public long unlike(Long postId, User user) {
        // 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("게시글", postId);
        }

        likeRepository.findByUserIdAndPostId(user.getId(), postId)
                .ifPresent(likeRepository::delete);

        return likeRepository.countByPostId(postId);
    }

    /**
     * 좋아요 토글 — like/unlike를 하나로 합치고 싶을 때의 대안 구현.
     * 누른 상태면 취소, 안 누른 상태면 추가합니다.
     *
     * @return 토글 후 좋아요 개수
     */
    @Transactional
    public long toggle(Long postId, User user) {
        Post post = findPostById(postId);

        likeRepository.findByUserIdAndPostId(user.getId(), postId)
                .ifPresentOrElse(
                        likeRepository::delete,                       // 이미 눌렀으면 → 취소
                        () -> likeRepository.save(new Like(user, post)) // 안 눌렀으면 → 추가
                );

        return likeRepository.countByPostId(postId);
    }

    /** 특정 게시글의 좋아요 개수 조회 */
    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    /** 게시글을 ID로 조회 (없으면 404) */
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글", postId));
    }
}
