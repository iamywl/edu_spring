package com.edu.jpa;

import com.edu.jpa.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 프록시 정체 폭로 데모 (개념서 Chapter 2 "애노테이션은 마법이 아니다: 프록시와 AOP" 참고)
 *
 * <p>이 러너는 애플리케이션이 시작될 때 MemberService Bean의 <b>실제 런타임 클래스 이름</b>을
 * 로그로 출력한다. 개발자가 작성한 클래스는 분명 {@code com.edu.jpa.service.MemberService}인데,
 * 실제로 주입되는 객체의 클래스 이름을 찍어보면
 * {@code com.edu.jpa.service.MemberService$$SpringCGLIB$$...} 처럼
 * <b>Spring이 만든 프록시(CGLIB 서브클래스)</b>가 나온다.
 *
 * <p>이것이 {@code @Transactional}이 "마법"이 아니라 "프록시"라는 증거다.
 * MemberService에 {@code @Transactional}이 붙어 있으므로, Spring은 원본 객체를 상속한
 * 프록시를 만들어 컨테이너에 등록한다. 우리가 Service 메서드를 호출하면
 * 사실은 이 프록시가 먼저 트랜잭션을 열고 → 진짜 메서드를 호출하고 → 커밋/롤백한다.
 *
 * <p>이 클래스는 로그만 출력할 뿐 애플리케이션 동작에 아무 영향을 주지 않는다.
 */
@Component
public class ProxyRevealRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProxyRevealRunner.class);

    private final MemberService memberService;

    public ProxyRevealRunner(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void run(String... args) {
        String runtimeClass = memberService.getClass().getName();
        boolean isProxy = runtimeClass.contains("$$"); // CGLIB 프록시는 클래스 이름에 $$가 들어간다

        log.info("========================================================================");
        log.info("[프록시 폭로] 내가 작성한 클래스   : com.edu.jpa.service.MemberService");
        log.info("[프록시 폭로] 실제 주입된 런타임 클래스: {}", runtimeClass);
        log.info("[프록시 폭로] 프록시 여부           : {}", isProxy ? "예 (Spring이 감싼 프록시)" : "아니오 (원본)");
        log.info("[프록시 폭로] 이유: MemberService에 @Transactional이 붙어 있어서,");
        log.info("[프록시 폭로]       Spring이 원본을 상속한 CGLIB 프록시를 만들어 주입했다.");
        log.info("[프록시 폭로]       메서드 호출 시 이 프록시가 트랜잭션을 열고→실제 메서드→커밋한다.");
        log.info("[프록시 폭로]       => 그래서 self-invocation(this.method())은 프록시를 우회한다!");
        log.info("========================================================================");
    }
}
