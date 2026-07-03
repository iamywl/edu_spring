package com.edu.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 모든 요청의 메서드/URI/소요 시간을 로깅하는 서블릿 필터.
 *
 * ★ 필터(Filter)는 "서블릿 컨테이너(톰캣) 레벨"의 기술이다 ★
 *  - jakarta.servlet.Filter는 Spring이 아니라 서블릿 표준 스펙의 인터페이스다.
 *  - 요청이 DispatcherServlet(= Spring MVC의 정문)에 "도달하기 전"에 실행되고,
 *    응답이 클라이언트로 나가기 "직전"에 다시 실행된다.
 *  - 즉 Spring MVC 바깥에 있으므로, 어떤 컨트롤러가 처리할지 같은
 *    Spring 내부 정보(HandlerMethod)는 여기서 알 수 없다.
 *  - Spring Security가 인증/인가를 바로 이 필터 체인 위에서 구현한다.
 *    (아직 DispatcherServlet에 들어가기 전이므로, 인증 실패 요청을
 *     컨트롤러 근처에도 못 가게 초입에서 차단할 수 있다)
 *
 * @Component : 필터도 Bean으로 등록하면 Spring Boot가 자동으로
 *              서블릿 컨테이너의 필터 체인에 등록해 준다.
 * @Order     : 필터가 여러 개일 때의 실행 순서 (숫자가 작을수록 먼저 실행)
 */
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    // System.out 대신 SLF4J Logger 사용 - 레벨/포맷/출력 위치를 설정으로 제어할 수 있다
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 서블릿 표준 인터페이스라 ServletRequest로 받는다. HTTP 정보를 쓰려면 다운캐스팅이 필요하다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long start = System.currentTimeMillis();
        log.info("[Filter] 요청 진입 → {} {} (아직 DispatcherServlet 도착 전)",
                httpRequest.getMethod(), httpRequest.getRequestURI());

        try {
            // chain.doFilter() = "다음 필터(없으면 DispatcherServlet)로 요청을 넘긴다"
            // 이 줄을 호출하지 않으면 요청이 여기서 차단된다 (Spring Security가 쓰는 방식)
            chain.doFilter(request, response);
        } finally {
            // 응답이 완성되어 돌아온 뒤 (컨트롤러/인터셉터 처리가 모두 끝난 후)
            long elapsed = System.currentTimeMillis() - start;
            log.info("[Filter] 응답 반환 ← {} {} - 상태: {}, 전체 소요: {}ms",
                    httpRequest.getMethod(), httpRequest.getRequestURI(),
                    httpResponse.getStatus(), elapsed);
        }
    }
}
