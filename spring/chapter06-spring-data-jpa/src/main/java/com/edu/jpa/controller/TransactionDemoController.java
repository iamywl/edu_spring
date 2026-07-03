package com.edu.jpa.controller;

import com.edu.jpa.dto.TxDemoResponse;
import com.edu.jpa.service.TransactionDemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Transactional 롤백·전파·readOnly 데모 컨트롤러
 *
 * <p>각 엔드포인트는 하나의 시나리오를 실행하고, "무슨 일이 일어났는지"를
 * 설명 문자열과 함께 반환한다. 실행 전후의 회원 수(memberCountBefore/After)를
 * 비교하면 커밋/롤백 여부를 응답만 보고도 판단할 수 있다.
 *
 * <p>일부러 서비스가 던지는 예외를 <b>컨트롤러에서 직접 catch</b>한다.
 * (GlobalExceptionHandler로 보내 500을 내는 대신, "예외가 났지만 그래서
 * 트랜잭션이 어떻게 됐는가"를 학습용 응답으로 풀어서 보여주기 위함이다.)
 *
 * <p>실행하면서 콘솔 로그의 {@code [TX-DEMO]} 와 Hibernate의 insert/update 문을
 * 함께 관찰하자. (application.yml의 show-sql: true)
 */
@RestController
@RequestMapping("/api/tx-demo")
public class TransactionDemoController {

    private final TransactionDemoService txDemoService;

    public TransactionDemoController(TransactionDemoService txDemoService) {
        this.txDemoService = txDemoService;
    }

    /**
     * [1-a] 런타임 예외 → 롤백 (기본 규칙)
     * POST /api/tx-demo/rollback-runtime
     */
    @PostMapping("/rollback-runtime")
    public ResponseEntity<TxDemoResponse> rollbackRuntime() {
        long before = txDemoService.countMembers();
        String caught = null;
        try {
            txDemoService.saveThenThrowRuntime(uniqueEmail("runtime"));
        } catch (IllegalStateException e) {
            caught = e.getMessage();
        }
        long after = txDemoService.countMembers();
        return ResponseEntity.ok(new TxDemoResponse(
                "롤백 기본 규칙 (1-a): RuntimeException → 롤백",
                "회원을 INSERT한 뒤 RuntimeException을 던졌다. 스프링 기본 규칙에 따라 트랜잭션이 롤백되어 INSERT가 취소됐다.",
                List.of(
                        "1) 트랜잭션 시작 → 회원 INSERT 실행 (콘솔에 insert 문이 보인다)",
                        "2) RuntimeException 발생: \"" + caught + "\"",
                        "3) 프록시가 unchecked 예외를 감지 → rollback",
                        "4) 회원 수 변화 " + before + " → " + after + " : 그대로! INSERT가 없던 일이 됐다 (롤백의 증거)"
                ),
                before, after));
    }

    /**
     * [1-b] checked 예외 → 커밋 (★ 기본 규칙의 함정)
     * POST /api/tx-demo/commit-checked
     */
    @PostMapping("/commit-checked")
    public ResponseEntity<TxDemoResponse> commitChecked() {
        long before = txDemoService.countMembers();
        String caught = null;
        try {
            txDemoService.saveThenThrowChecked(uniqueEmail("checked"));
        } catch (TransactionDemoService.DemoCheckedException e) {
            caught = e.getMessage();
        }
        long after = txDemoService.countMembers();
        return ResponseEntity.ok(new TxDemoResponse(
                "롤백 기본 규칙 (1-b): checked 예외 → 커밋(!)",
                "회원을 INSERT한 뒤 checked 예외를 던졌지만, 스프링 기본 규칙은 checked 예외에 롤백하지 않는다. 예외가 났는데도 커밋됐다!",
                List.of(
                        "1) 트랜잭션 시작 → 회원 INSERT 실행",
                        "2) checked 예외 발생: \"" + caught + "\"",
                        "3) 프록시의 기본 롤백 규칙은 RuntimeException/Error만 대상 → checked 예외는 commit",
                        "4) 회원 수 변화 " + before + " → " + after + " : 1 증가! 예외가 났는데도 저장됐다 (커밋의 증거)",
                        "→ 이 동작을 바꾸고 싶으면 rollbackFor 옵션을 쓴다. 다음 엔드포인트(rollback-for)와 비교해 보자."
                ),
                before, after));
    }

    /**
     * [1-c] rollbackFor 지정 → checked 예외도 롤백
     * POST /api/tx-demo/rollback-for
     */
    @PostMapping("/rollback-for")
    public ResponseEntity<TxDemoResponse> rollbackFor() {
        long before = txDemoService.countMembers();
        String caught = null;
        try {
            txDemoService.saveThenThrowCheckedWithRollbackFor(uniqueEmail("rollbackfor"));
        } catch (TransactionDemoService.DemoCheckedException e) {
            caught = e.getMessage();
        }
        long after = txDemoService.countMembers();
        return ResponseEntity.ok(new TxDemoResponse(
                "롤백 기본 규칙 (1-c): rollbackFor로 checked 예외도 롤백",
                "commit-checked와 코드는 같지만 @Transactional(rollbackFor = DemoCheckedException.class)를 지정했다. 이번에는 checked 예외에도 롤백된다.",
                List.of(
                        "1) 트랜잭션 시작 → 회원 INSERT 실행",
                        "2) checked 예외 발생: \"" + caught + "\"",
                        "3) rollbackFor에 지정된 예외 타입 → rollback",
                        "4) 회원 수 변화 " + before + " → " + after + " : 그대로! (1-b와 결과가 다른 이유는 옵션 하나 차이)"
                ),
                before, after));
    }

    /**
     * [2-a] 전파 REQUIRED: 내부 실패 → 전체 롤백 + UnexpectedRollbackException
     * POST /api/tx-demo/propagation-required
     */
    @PostMapping("/propagation-required")
    public ResponseEntity<TxDemoResponse> propagationRequired() {
        long before = txDemoService.countMembers();
        String caught = null;
        try {
            txDemoService.outerWithRequiredInner(uniqueEmail("req-outer"), uniqueEmail("req-inner"));
        } catch (UnexpectedRollbackException e) {
            caught = e.getClass().getSimpleName();
        }
        long after = txDemoService.countMembers();
        return ResponseEntity.ok(new TxDemoResponse(
                "전파 (2-a): REQUIRED — 내부 실패가 전체를 롤백시킨다",
                "바깥과 내부가 같은 트랜잭션을 공유(REQUIRED)한다. 내부 예외를 바깥에서 catch했지만, 트랜잭션에는 이미 rollback-only 마크가 찍혀 커밋 시도 시 " + caught + "이 발생했고 바깥 INSERT까지 전부 롤백됐다.",
                List.of(
                        "1) 바깥 트랜잭션 시작 → 바깥 회원 INSERT",
                        "2) 내부 호출(REQUIRED) → 같은 트랜잭션에 합류 (로그에서 트랜잭션 이름이 바깥과 같다)",
                        "3) 내부 회원 INSERT 후 예외 → 공유 트랜잭션에 rollback-only 마크",
                        "4) 바깥이 예외를 catch하고 정상 종료 → 프록시가 커밋 시도",
                        "5) rollback-only 마크 발견 → 전체 롤백 + " + caught + " 발생",
                        "6) 회원 수 변화 " + before + " → " + after + " : 그대로! 바깥·내부 회원 모두 사라졌다",
                        "→ 교훈: REQUIRED로 합류한 내부의 예외는 catch해도 소용없다. 이미 '한 배'를 탔기 때문이다."
                ),
                before, after));
    }

    /**
     * [2-b] 전파 REQUIRES_NEW: 내부만 롤백, 바깥은 커밋
     * POST /api/tx-demo/propagation-requires-new
     */
    @PostMapping("/propagation-requires-new")
    public ResponseEntity<TxDemoResponse> propagationRequiresNew() {
        long before = txDemoService.countMembers();
        txDemoService.outerWithRequiresNewInner(uniqueEmail("new-outer"), uniqueEmail("new-inner"));
        long after = txDemoService.countMembers();
        return ResponseEntity.ok(new TxDemoResponse(
                "전파 (2-b): REQUIRES_NEW — 내부만 롤백되고 바깥은 커밋된다",
                "내부가 REQUIRES_NEW로 '별도의 새 트랜잭션'을 열었다. 내부는 예외로 롤백됐지만 바깥 트랜잭션은 오염되지 않아 바깥 회원은 정상 커밋됐다.",
                List.of(
                        "1) 바깥 트랜잭션(A) 시작 → 바깥 회원 INSERT",
                        "2) 내부 호출(REQUIRES_NEW) → A는 보류(suspend), 새 트랜잭션(B) 시작 (로그에서 트랜잭션 이름이 다르다)",
                        "3) 내부 회원 INSERT 후 예외 → B만 롤백 (내부 회원만 사라짐)",
                        "4) 바깥이 예외를 catch → A에는 rollback-only 마크가 없다",
                        "5) 바깥 정상 종료 → A 커밋",
                        "6) 회원 수 변화 " + before + " → " + after + " : 1 증가! '바깥회원(REQ_NEW)'만 저장됐다",
                        "→ propagation-required(전체 롤백)와 결과를 비교해 보자. 전파 속성 하나로 운명이 갈린다."
                ),
                before, after));
    }

    /**
     * [3] readOnly=true: 변경 감지가 flush되지 않는다
     * POST /api/tx-demo/read-only
     */
    @PostMapping("/read-only")
    public ResponseEntity<TxDemoResponse> readOnly() {
        long before = txDemoService.countMembers();
        // 준비: 실습용 회원을 하나 만들어 커밋 (이름: "원본이름")
        Long id = txDemoService.prepareReadOnlyMember(uniqueEmail("readonly"));
        // readOnly=true 트랜잭션 안에서 이름 변경을 "시도"
        txDemoService.tryUpdateInReadOnly(id, "변경시도된이름");
        // 새 트랜잭션으로 다시 조회 → 이름이 바뀌었을까?
        String actualName = txDemoService.getMemberName(id);
        long after = txDemoService.countMembers();
        return ResponseEntity.ok(new TxDemoResponse(
                "readOnly (3): readOnly=true에서는 dirty checking이 flush되지 않는다",
                "readOnly=true 트랜잭션 안에서 setName(\"변경시도된이름\")을 호출했지만, flush가 생략되어 UPDATE가 나가지 않았다. 다시 조회한 이름은 여전히 \"" + actualName + "\"이다.",
                List.of(
                        "1) 준비: id=" + id + " 회원을 이름 '원본이름'으로 저장(커밋)",
                        "2) readOnly=true 트랜잭션에서 조회 후 setName('변경시도된이름') 호출",
                        "3) 일반 트랜잭션이라면 커밋 시점에 변경 감지 → UPDATE 자동 실행 (MemberService.updateMember 참고)",
                        "4) 그러나 readOnly=true는 FlushMode를 MANUAL로 바꿔 flush 생략 → 콘솔에 UPDATE 문이 없다!",
                        "5) 새 트랜잭션으로 재조회한 이름: \"" + actualName + "\" (그대로)",
                        "→ readOnly는 '실수로 인한 쓰기 방지 + 스냅샷 비교 생략으로 인한 성능 이점'을 준다. 읽기 전용 메서드에 붙이는 이유다."
                ),
                before, after));
    }

    /** 이메일 UNIQUE 제약이 있으므로 호출할 때마다 겹치지 않는 이메일을 만든다 */
    private String uniqueEmail(String prefix) {
        return "tx-" + prefix + "-" + System.nanoTime() + "@demo.com";
    }
}
