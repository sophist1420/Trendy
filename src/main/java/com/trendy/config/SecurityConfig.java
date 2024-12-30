package com.trendy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.trendy.login.CustomAuthenticationFailureHandler;
import com.trendy.login.CustomOAuth2LoginSuccessHandler;
import com.trendy.login.CustomOAuth2UserService;
import com.trendy.login.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;

    // CustomOAuth2UserService와 CustomUserDetailsService를 주입받는 생성자
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomUserDetailsService customUserDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customUserDetailsService = customUserDetailsService;
    }

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 필요 시 생성
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/loginFailure", "check-username", "/signup", "/oauth2/**").permitAll() // 공용 경로 허용
                .requestMatchers("/mypage", "/reviews/**").hasRole("USER") // 권한이 필요한 경로
                .anyRequest().authenticated() // 나머지 경로 인증 필요
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // OAuth2 로그인 페이지
                .successHandler(new CustomOAuth2LoginSuccessHandler()) // 커스터마이징 핸들러 등록
                .failureHandler(new CustomAuthenticationFailureHandler())
                .userInfoEndpoint()
                .userService(customOAuth2UserService) // 사용자 정보 서비스 등록
            )
            .formLogin(form -> form
                .loginPage("/login") // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/perform_login") // 로그인 처리 URL
                .defaultSuccessUrl("/loginSuccess", true) // 로그인 성공 후 리다이렉트
                .failureUrl("/loginFailure")
                .permitAll() // 로그인 페이지 접근 허용
            )
            .logout(logout -> logout
                .logoutUrl("/perform_logout") // 로그아웃 처리 URL
                .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트
                .permitAll()
            )
        	.csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 정의
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService) // CustomUserDetailsService 등록
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
