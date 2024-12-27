package com.trendy.login;

import com.trendy.login.CustomAuthenticationFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // API 경로에서 CSRF 비활성화
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/loginFailure").permitAll() // 인증 없이 접근 가능
                .requestMatchers("/mypage").authenticated() // /mypage는 인증 필요
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login") // 사용자 정의 로그인 페이지
                .defaultSuccessUrl("/loginSuccess", true) // 로그인 성공 후 이동 경로
                .failureHandler(new CustomAuthenticationFailureHandler()) // 로그인 실패 시 핸들러 호출
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
