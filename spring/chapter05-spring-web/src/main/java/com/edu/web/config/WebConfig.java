package com.edu.web.config;

import com.edu.web.interceptor.TimingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 관련 전역 설정 클래스.
 *
 * CORS(Cross-Origin Resource Sharing, 교차 출처 리소스 공유)란?
 *  - 브라우저는 보안을 위해 "같은 출처(origin)"가 아닌 곳으로의 요청을 기본적으로 차단한다.
 *    (출처 = 프로토콜 + 호스트 + 포트, 예: http://localhost:3000)
 *  - 예를 들어 프론트엔드(http://localhost:3000)에서 백엔드 API(http://localhost:8080)를
 *    호출하면 포트가 달라 "다른 출처"이므로 브라우저가 응답을 막는다(CORS 에러).
 *  - 서버에서 "이 출처는 허용한다"고 알려주면 브라우저가 요청을 통과시킨다.
 *
 * WebMvcConfigurer를 구현하면 Spring MVC의 다양한 설정을 커스터마이징할 수 있고,
 * addCorsMappings()로 전역 CORS 정책을, addInterceptors()로 인터셉터 등록을 정의한다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 생성자 주입 (권장 방식)
    private final TimingInterceptor timingInterceptor;

    public WebConfig(TimingInterceptor timingInterceptor) {
        this.timingInterceptor = timingInterceptor;
    }

    /**
     * 인터셉터 등록.
     *
     * 필터(@Component만 붙이면 자동 등록)와 달리, 인터셉터는 Spring MVC 내부 개념이라
     * 이렇게 WebMvcConfigurer.addInterceptors()에 직접 등록해야 동작한다.
     * addPathPatterns/excludePathPatterns로 적용 범위를 세밀하게 지정할 수 있다.
     * (이것도 필터와의 차이점 - 필터의 URL 패턴 매칭은 서블릿 스펙 수준이라 단순하다)
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timingInterceptor)
                .addPathPatterns("/api/**");   // /api 로 시작하는 경로에만 적용
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")               // /api 로 시작하는 모든 경로에 적용
                .allowedOrigins("http://localhost:3000")  // 허용할 출처(개발용 프론트엔드 주소)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메서드
                .allowedHeaders("*")                  // 허용할 요청 헤더 (전체 허용)
                .allowCredentials(true)               // 쿠키 등 인증 정보 포함 요청 허용
                .maxAge(3600);                        // 사전 요청(preflight) 결과 캐시 시간(초)
    }
}
