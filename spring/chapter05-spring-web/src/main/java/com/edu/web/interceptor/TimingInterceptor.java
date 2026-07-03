package com.edu.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 컨트롤러 실행 시간을 측정하는 인터셉터.
 *
 * ★ 인터셉터(Interceptor)는 "Spring MVC 레벨"의 기술이다 ★
 *  - HandlerInterceptor는 서블릿 표준이 아니라 Spring이 제공하는 인터페이스다.
 *  - DispatcherServlet "안쪽"에서, 컨트롤러(핸들러) 호출 앞뒤에 끼어든다.
 *  - Spring 내부에 있으므로 "어떤 컨트롤러의 어떤 메서드가 처리하는지"(HandlerMethod)를
 *    알 수 있다. 이것이 필터와의 결정적 차이다.
 *  - WebConfig.addInterceptors()에 등록해야 동작한다 (@Component만으로는 부족!)
 *
 * 세 가지 콜백의 호출 시점:
 *  ① preHandle       : 컨트롤러 실행 "전"  → false를 반환하면 컨트롤러 진입 차단
 *  ② postHandle      : 컨트롤러 정상 반환 "후", 응답 완성 전 → 예외가 나면 호출되지 않음
 *  ③ afterCompletion : 요청 처리 완전 종료 후 → 예외가 나도 "항상" 호출됨 (자원 정리용)
 */
@Component
public class TimingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TimingInterceptor.class);

    // 시작 시각을 담아둘 request attribute 키
    // (인터셉터는 싱글톤 Bean이라 필드에 저장하면 여러 요청이 섞인다!
    //  요청별 데이터는 반드시 request에 붙여서 전달한다)
    private static final String START_TIME_ATTRIBUTE = "timing.startTime";

    /**
     * ① 컨트롤러 실행 전.
     * - 여기서 할 수 있는 것: 요청 검사, 인증 확인, 컨트롤러 진입 차단(false 반환)
     * - handler 파라미터로 "누가 이 요청을 처리할지"를 미리 알 수 있다
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // handler는 보통 HandlerMethod(컨트롤러의 특정 메서드)다.
        // 정적 리소스 요청 등은 아닐 수 있으므로 instanceof로 확인한다.
        if (handler instanceof HandlerMethod handlerMethod) {
            log.info("[Interceptor] preHandle: 이 요청은 {}.{}() 가 처리합니다",
                    handlerMethod.getBeanType().getSimpleName(),
                    handlerMethod.getMethod().getName());
        }
        return true;  // true = 계속 진행, false = 여기서 요청 처리 중단
    }

    /**
     * ② 컨트롤러가 "정상적으로" 반환한 후.
     * - 여기서 할 수 있는 것: 컨트롤러 처리 결과 확인, (뷰 방식이라면) 모델에 공통 데이터 추가
     * - 컨트롤러에서 예외가 발생하면 이 메서드는 건너뛴다
     * - @RestController는 뷰를 쓰지 않으므로 modelAndView는 null이다
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        log.info("[Interceptor] postHandle: 컨트롤러 정상 반환 (예외 시에는 호출되지 않음)");
    }

    /**
     * ③ 요청 처리가 완전히 끝난 후 (뷰 렌더링/응답 직렬화까지 완료).
     * - 예외 발생 여부와 무관하게 항상 호출된다 → 측정 마무리, 자원 정리에 적합
     * - 발생한 예외가 ex 파라미터로 전달된다 (정상이면 null)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        Long start = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long elapsed = (start != null) ? System.currentTimeMillis() - start : -1;

        if (ex != null) {
            log.warn("[Interceptor] afterCompletion: 처리 중 예외 발생 - {} ({}ms)",
                    ex.getClass().getSimpleName(), elapsed);
        } else {
            log.info("[Interceptor] afterCompletion: 핸들러 구간 소요 {}ms (항상 호출됨)", elapsed);
        }
    }
}
