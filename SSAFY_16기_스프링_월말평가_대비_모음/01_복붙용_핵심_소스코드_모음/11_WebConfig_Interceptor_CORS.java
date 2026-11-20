package com.ssafy.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * [Interceptor & CORS 설정 파일]
 * - 로그인 체크, 권한 체크 등 컨트롤러 들어가기 전(preHandle)에 문지기 역할을 합니다.
 * - 필요 없으면 클래스 만들지 마세요! (로그인 체크 문제 나올 때만 사용)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/api/order/**") // 검사할 URL 경로
                .excludePathPatterns("/api/product", "/swagger-ui/**", "/v3/api-docs/**"); // 통과시킬 경로
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 프론트엔드 주소 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }

    // 문지기 클래스: preHandle이 true를 돌려주면 통과, false를 돌려주면 컨트롤러 실행 차단
    static class LoginCheckInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("loginUser") == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 반환
                return false;
            }
            return true;
        }
    }
}
