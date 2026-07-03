package com.edu.jpa.service;

import com.edu.jpa.entity.Member;
import com.edu.jpa.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @Transactional 롤백·전파·readOnly 실습 서비스
 *
 * <p>기존 Member 도메인을 그대로 재사용해, {@code @Transactional}의 옵션이 실제로
 * 어떻게 동작하는지 "회원이 저장되었는가/사라졌는가"로 확인한다.
 * (Member의 PK 전략이 IDENTITY라 {@code save()} 시점에 INSERT가 <b>즉시 실행</b>된다.
 * 즉, 롤백은 "이미 실행된 INSERT를 되돌리는 것"임을 눈으로 볼 수 있다.)
 *
 * <p>실습 시나리오
 * <ol>
 *   <li>롤백 기본 규칙: RuntimeException → 롤백 / checked 예외 → 커밋(기본값) / rollbackFor로 변경</li>
 *   <li>전파(Propagation): REQUIRED(합류) vs REQUIRES_NEW(새 트랜잭션) — {@link TransactionInnerService} 참고</li>
 *   <li>readOnly=true: 변경 감지(dirty checking)가 flush되지 않음</li>
 * </ol>
 *
 * <p>각 메서드는 {@code TransactionSynchronizationManager}로 현재 트랜잭션의
 * 이름/활성 여부를 로그에 찍는다. 콘솔에서 {@code [TX-DEMO]}를 검색하며 따라가 보자.
 */
@Service
public class TransactionDemoService {

    private static final Logger log = LoggerFactory.getLogger(TransactionDemoService.class);

    private final MemberRepository memberRepository;

    /**
     * ★ 전파 실습용 내부 서비스는 반드시 "별도의 빈"으로 주입받는다.
     * 같은 클래스의 메서드를 this.method()로 직접 호출하면 프록시를 거치지 않아
     * REQUIRES_NEW 같은 전파 설정이 조용히 무시된다 (self-invocation 함정).
     * 자세한 설명은 TransactionInnerService의 클래스 주석 참고.
     */
    private final TransactionInnerService innerService;

    public TransactionDemoService(MemberRepository memberRepository,
                                  TransactionInnerService innerService) {
        this.memberRepository = memberRepository;
        this.innerService = innerService;
    }

    // =====================================================================
    // 시나리오 1. 롤백 기본 규칙
    // =====================================================================

    /**
     * [1-a] RuntimeException(unchecked) → <b>롤백</b> (스프링 기본 규칙)
     *
     * <p>회원을 저장(INSERT 실행)한 뒤 런타임 예외를 던진다.
     * 프록시가 예외를 감지하고 트랜잭션을 롤백하므로, INSERT는 없던 일이 된다.
     */
    @Transactional
    public void saveThenThrowRuntime(String email) {
        logTxStatus("rollback-runtime");
        memberRepository.save(new Member("롤백테스트(런타임)", email));
        log.info("[TX-DEMO] INSERT 실행됨(아직 커밋 전). 이제 RuntimeException을 던진다 → 롤백 기대");
        throw new IllegalStateException("런타임 예외 발생! (스프링 기본 규칙: unchecked → rollback)");
    }

    /**
     * [1-b] checked 예외 → <b>커밋</b> (스프링 기본 규칙!)
     *
     * <p>많은 학생이 "@Transactional은 예외가 나면 무조건 롤백"이라고 오해한다.
     * 스프링의 기본 롤백 규칙은 <b>RuntimeException과 Error에 대해서만</b> 롤백이다.
     * checked 예외(Exception 상속)는 "호출자가 처리(복구)할 수 있는 예외"로 간주해
     * <b>커밋</b>해 버린다. → 이 메서드 실행 후 회원 수가 1 늘어 있다!
     */
    @Transactional
    public void saveThenThrowChecked(String email) throws DemoCheckedException {
        logTxStatus("commit-checked");
        memberRepository.save(new Member("커밋테스트(체크예외)", email));
        log.info("[TX-DEMO] INSERT 실행됨. 이제 checked 예외를 던진다 → 기본 규칙에서는 '커밋'된다!");
        throw new DemoCheckedException("체크 예외 발생! (스프링 기본 규칙: checked → commit)");
    }

    /**
     * [1-c] rollbackFor로 기본 규칙 바꾸기 → checked 예외도 <b>롤백</b>
     *
     * <p>[1-b]와 코드가 완전히 같고, 애너테이션 옵션 하나만 다르다.
     * {@code rollbackFor = DemoCheckedException.class}를 지정하면
     * 해당 checked 예외에서도 롤백한다. (실무에서는 {@code rollbackFor = Exception.class}로
     * "모든 예외에 롤백"을 지정하는 경우도 많다.)
     */
    @Transactional(rollbackFor = DemoCheckedException.class)
    public void saveThenThrowCheckedWithRollbackFor(String email) throws DemoCheckedException {
        logTxStatus("rollback-for");
        memberRepository.save(new Member("롤백테스트(rollbackFor)", email));
        log.info("[TX-DEMO] INSERT 실행됨. checked 예외를 던지지만 rollbackFor 지정 → 이번엔 롤백된다");
        throw new DemoCheckedException("체크 예외 발생! (rollbackFor 지정 → rollback)");
    }

    // =====================================================================
    // 시나리오 2. 전파(Propagation)
    // =====================================================================

    /**
     * [2-a] REQUIRED(기본): 내부가 실패하면 <b>전체가 롤백</b>된다
     *
     * <p>흐름:
     * <pre>
     *  바깥 트랜잭션 시작
     *    ├─ 바깥 회원 INSERT
     *    ├─ 내부 서비스 호출 (REQUIRED → 같은 트랜잭션에 합류)
     *    │    ├─ 내부 회원 INSERT
     *    │    └─ 예외! → 공유 트랜잭션에 rollback-only 마크
     *    ├─ 바깥이 예외를 catch (그래서 "복구했다"고 착각하기 쉽다)
     *    └─ 바깥 정상 종료 → 프록시가 커밋 시도
     *         → 그러나 rollback-only 마크 발견 → 롤백 + UnexpectedRollbackException!
     * </pre>
     *
     * <p>핵심: <b>바깥에서 내부 예외를 잡아도 이미 늦었다.</b> REQUIRED로 합류한
     * 내부의 실패는 "같은 트랜잭션"의 실패이므로 바깥 INSERT까지 모두 사라진다.
     * 이 메서드를 호출한 컨트롤러는 {@code UnexpectedRollbackException}을 받는다.
     */
    @Transactional
    public void outerWithRequiredInner(String outerEmail, String innerEmail) {
        logTxStatus("바깥(outer) - REQUIRED 실습");
        memberRepository.save(new Member("바깥회원(REQUIRED)", outerEmail));
        log.info("[TX-DEMO] 바깥 회원 INSERT 실행됨. 이제 내부(REQUIRED) 호출");
        try {
            innerService.saveInnerThenFailRequired(innerEmail);
        } catch (RuntimeException e) {
            // 예외를 잡았으니 괜찮겠지? → 아니다! 트랜잭션은 이미 rollback-only 상태다.
            log.info("[TX-DEMO] 바깥에서 내부 예외를 catch함: '{}' — 하지만 트랜잭션은 이미 rollback-only!", e.getMessage());
        }
        log.info("[TX-DEMO] 바깥 메서드 정상 종료 → 프록시가 커밋 시도 → UnexpectedRollbackException 발생 예정");
    }

    /**
     * [2-b] REQUIRES_NEW: 내부만 롤백되고 <b>바깥은 커밋</b>된다
     *
     * <p>흐름:
     * <pre>
     *  바깥 트랜잭션(A) 시작
     *    ├─ 바깥 회원 INSERT (트랜잭션 A)
     *    ├─ 내부 서비스 호출 (REQUIRES_NEW → A는 보류, 새 트랜잭션 B 시작)
     *    │    ├─ 내부 회원 INSERT (트랜잭션 B)
     *    │    └─ 예외! → B만 롤백 (내부 회원만 사라짐)
     *    ├─ 바깥이 예외를 catch → A는 멀쩡함 (rollback-only 마크 없음)
     *    └─ 바깥 정상 종료 → A 커밋 → 바깥 회원은 저장됨!
     * </pre>
     *
     * <p>실무 활용 예: 본 작업이 실패해도 "감사 로그/이력"은 반드시 남겨야 할 때
     * 이력 저장을 REQUIRES_NEW로 분리한다. (단, 커넥션을 2개 쓰므로 남용 금지)
     */
    @Transactional
    public void outerWithRequiresNewInner(String outerEmail, String innerEmail) {
        logTxStatus("바깥(outer) - REQUIRES_NEW 실습");
        memberRepository.save(new Member("바깥회원(REQ_NEW)", outerEmail));
        log.info("[TX-DEMO] 바깥 회원 INSERT 실행됨. 이제 내부(REQUIRES_NEW) 호출 → 바깥 트랜잭션은 잠시 보류됨");
        try {
            innerService.saveInnerThenFailRequiresNew(innerEmail);
        } catch (RuntimeException e) {
            // REQUIRES_NEW는 "별도의 트랜잭션"이므로, 내부의 실패가 바깥을 오염시키지 않는다.
            log.info("[TX-DEMO] 바깥에서 내부 예외를 catch함: '{}' — 내부 트랜잭션만 롤백됐고 바깥은 무사!", e.getMessage());
        }
        log.info("[TX-DEMO] 바깥 메서드 정상 종료 → 바깥 트랜잭션 커밋 → 바깥 회원은 저장된다");
    }

    // =====================================================================
    // 시나리오 3. readOnly=true — 변경 감지(dirty checking)가 flush되지 않는다
    // =====================================================================

    /**
     * [3-준비] readOnly 실습용 회원을 하나 만들어 커밋해 둔다.
     */
    @Transactional
    public Long prepareReadOnlyMember(String email) {
        Member member = memberRepository.save(new Member("원본이름", email));
        log.info("[TX-DEMO] readOnly 실습용 회원 저장: id={}, name={}", member.getId(), member.getName());
        return member.getId();
    }

    /**
     * [3-a] readOnly=true 트랜잭션 안에서 엔티티 값을 바꿔 본다.
     *
     * <p>일반 트랜잭션이라면 (MemberService.updateMember처럼) 커밋 시점에
     * 변경 감지(dirty checking)가 스냅샷과 비교해 UPDATE를 자동 실행한다.
     *
     * <p>그러나 {@code readOnly = true}면 Hibernate가 세션의 FlushMode를 MANUAL로 바꾸고
     * 스냅샷 유지·flush를 생략한다. → {@code setName()}을 호출해도 <b>UPDATE가 나가지 않는다.</b>
     * 콘솔의 show-sql 로그에서 UPDATE 문이 없는 것을 직접 확인하자.
     */
    @Transactional(readOnly = true)
    public void tryUpdateInReadOnly(Long id, String newName) {
        logTxStatus("read-only"); // readOnly=true 로 찍히는지 확인
        Member member = memberRepository.findById(id).orElseThrow();
        log.info("[TX-DEMO] 조회한 이름: '{}' → setName('{}') 호출 (일반 트랜잭션이면 UPDATE가 나갈 상황)", member.getName(), newName);
        member.setName(newName); // 값은 바꾸지만...
        log.info("[TX-DEMO] readOnly=true 이므로 커밋 시점에 flush 생략 → UPDATE 없음 (로그에서 확인!)");
    }

    /**
     * [3-b] 새 트랜잭션으로 다시 조회해서 이름이 그대로인지 확인한다.
     */
    @Transactional(readOnly = true)
    public String getMemberName(Long id) {
        return memberRepository.findById(id).orElseThrow().getName();
    }

    // =====================================================================
    // 보조
    // =====================================================================

    /** 시나리오 전후 회원 수 비교용 (커밋/롤백의 증거) */
    @Transactional(readOnly = true)
    public long countMembers() {
        return memberRepository.count();
    }

    /** 현재 스레드의 트랜잭션 이름/활성 여부/readOnly를 로그로 출력 */
    private void logTxStatus(String where) {
        log.info("[TX-DEMO] {} | 트랜잭션 이름={} | 활성={} | readOnly={}",
                where,
                TransactionSynchronizationManager.getCurrentTransactionName(),
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    }

    /**
     * 실습용 checked 예외.
     * RuntimeException이 아니라 Exception을 상속 → 스프링 기본 규칙에서는 롤백 대상이 아니다.
     */
    public static class DemoCheckedException extends Exception {
        public DemoCheckedException(String message) {
            super(message);
        }
    }
}
