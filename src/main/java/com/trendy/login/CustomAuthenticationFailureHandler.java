package com.trendy.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        // 클라이언트 요청 정보 기록
        String clientIP = request.getRemoteAddr();
        String requestedPath = request.getRequestURI();

        logger.error("로그인 실패: {}, 클라이언트 IP: {}, 요청 경로: {}", exception.getMessage(), clientIP, requestedPath);

        // 사용자 친화적인 메시지 생성
        String errorMessage = getErrorMessage(exception);

        // 세션에 에러 메시지 저장
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", errorMessage);

        // 로그인 실패 페이지로 리다이렉트
        response.sendRedirect("/loginFailure");
    }

    // 예외 유형에 따른 사용자 친화적인 메시지 생성
    private String getErrorMessage(AuthenticationException exception) {
        if (exception.getMessage().contains("Bad credentials")) {
            return "아이디 또는 비밀번호가 올바르지 않습니다.";
        } else if (exception.getMessage().contains("User is disabled")) {
            return "계정이 비활성화되었습니다. 관리자에게 문의하세요.";
        } else if (exception.getMessage().contains("User account is locked")) {
            return "계정이 잠겼습니다. 관리자에게 문의하세요.";
        }
        return "로그인 중 알 수 없는 오류가 발생했습니다. 다시 시도해주세요.";
    }
}
