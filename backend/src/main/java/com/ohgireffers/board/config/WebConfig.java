package com.ohgireffers.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
// 이 클래스가 설정을 제공하는 클래스라고 선언
public class WebConfig implements WebMvcConfigurer {
    // 애플리케이션의 다양한 측면을 설정하고 사용자 정의할 수 있도록 콜백 메서드를 제공하는 인터페이스




    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //cors란 다른 도메인에서 내 서버의 API를 호출할 수 있는지 결정하는 보안 정책"
        //corsregistry cors설정을 편리하게 관리할 수 있도록 제공하는 클래스
        //다양한 CORS 규칙을 추가하고 설정하는 데 사용되는 빌더(builder) 패턴을 제공
        registry.addMapping("/**")
                //모든 url 경로에 대해 cors 정책을 적용
                .allowedOrigins("http://localhost:3000")
                // 해당 출처에서 오는 요청만 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                //허용할 http 메서드 자정 모든 메서드를 허용해줌
                .allowedHeaders("*")
                .allowCredentials(true)
                //쿠키 인증정보 포함 허용
                .maxAge(3600);
        //사전요청 결과를 3600초 동안 브라우저에 캐싱

        // 사전 요청이란 cors매커니즘의 일부로 브라우저가 실제 요청을
        // 보내기전에 서버에 특정 종류의 요청을 보내는것 브라우저가 서버에게 이런 요청을 보낼건데 허용하나? 미리 확인
    }
}