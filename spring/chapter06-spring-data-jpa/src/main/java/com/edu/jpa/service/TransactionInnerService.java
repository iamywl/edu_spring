package com.edu.jpa.service;

import com.edu.jpa.entity.Member;
import com.edu.jpa.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 전파(Propagation) 실습용 "내부 작업" 서비스
 *
 * <p><b>★ 왜 별도의 빈(클래스)으로 분리했는가 — self-invocation 함정</b>
 *
 * <p>{@code @Transactional}은 마법이 아니라 <b>프록시</b>로 동작한다(ProxyRevealRunner 참고).
 * 트랜잭션을 시작/참여/커밋/롤백하는 코드는 원본 객체가 아니라 <b>Spring이 만든 프록시</b>에 들어 있다.
 * 따라서 트랜잭션 설정(전파 속성 포함)이 적용되려면 반드시 <b>프록시를 거쳐서</b> 메서드가 호출되어야 한다.
 *
 * <pre>
 *  [다른 빈] ──> [프록시] ──> [원본 객체의 메서드]     : 트랜잭션 적용 O
 *  [원본 객체] ── this.내부메서드() ──> [원본 메서드]  : 프록시를 안 거침 → 전파 설정 무시!
 * </pre>
 *
 * <p>만약 {@code TransactionDemoService} 안에 {@code REQUIRES_NEW} 메서드를 만들고
 * 같은 클래스에서 {@code this.method()}로 호출하면, 프록시를 거치지 않으므로
 * <b>REQUIRES_NEW가 조용히 무시되고 바깥 트랜잭션에 그냥 합류</b>해 버린다.
 * (에러도 안 나서 더 위험하다!)
 *
 * <p>그래서 "내부 트랜잭션" 메서드는 이렇게 <b>별도의 빈</b>으로 분리하고,
 * 바깥 서비스가 이 빈을 <b>주입받아 호출</b>해야 프록시를 타고 전파 속성이 적용된다.
 */
@Service
public class TransactionInnerService {

    private static final Logger log = LoggerFactory.getLogger(TransactionInnerService.class);

    private final MemberRepository memberRepository;

    public TransactionInnerService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * [내부 작업 - REQUIRED (기본값)]
     * "이미 트랜잭션이 있으면 <b>거기에 합류</b>하고, 없으면 새로 만든다."
     *
     * <p>바깥 트랜잭션에 합류했으므로, 여기서 예외가 나면 Spring은 공유 중인
     * 트랜잭션 전체에 <b>rollback-only 마크</b>를 찍는다.
     * 바깥에서 예외를 catch해도 이 마크는 지워지지 않는다 →
     * 바깥이 커밋을 시도하는 순간 {@code UnexpectedRollbackException}이 터진다.
     */
    @Transactional(propagation = Propagation.REQUIRED) // 기본값이지만 학습을 위해 명시
    public void saveInnerThenFailRequired(String email) {
        logTxStatus("내부(REQUIRED)"); // 트랜잭션 이름이 바깥과 "같다" = 같은 트랜잭션에 합류했다
        memberRepository.save(new Member("내부회원(REQUIRED)", email));
        log.info("[TX-DEMO] 내부 회원 INSERT 실행됨. 이제 예외를 던진다 → 공유 트랜잭션에 rollback-only 마크!");
        throw new IllegalStateException("내부(REQUIRED) 작업 실패!");
    }

    /**
     * [내부 작업 - REQUIRES_NEW]
     * "바깥 트랜잭션이 있어도 <b>잠시 보류(suspend)</b>시키고, <b>내 트랜잭션을 새로 시작</b>한다."
     *
     * <p>물리적으로 <b>별도의 트랜잭션(별도의 DB 커넥션)</b>이므로,
     * 여기서 예외가 나서 롤백되어도 바깥 트랜잭션은 영향을 받지 않는다.
     * 바깥이 예외를 catch하고 정상 종료하면 바깥의 작업은 그대로 커밋된다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveInnerThenFailRequiresNew(String email) {
        logTxStatus("내부(REQUIRES_NEW)"); // 트랜잭션 이름이 바깥과 "다르다" = 새 트랜잭션이 시작됐다
        memberRepository.save(new Member("내부회원(REQ_NEW)", email));
        log.info("[TX-DEMO] 내부 회원 INSERT 실행됨. 이제 예외를 던진다 → '내 트랜잭션만' 롤백!");
        throw new IllegalStateException("내부(REQUIRES_NEW) 작업 실패!");
    }

    /**
     * 현재 스레드에 바인딩된 트랜잭션의 이름/활성 여부/readOnly를 로그로 출력.
     * 전파 실습에서 "지금 어느 트랜잭션 안에 있는가"를 눈으로 확인하는 용도.
     */
    private void logTxStatus(String where) {
        log.info("[TX-DEMO] {} | 트랜잭션 이름={} | 활성={} | readOnly={}",
                where,
                TransactionSynchronizationManager.getCurrentTransactionName(),
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    }
}
