package com.trendy.login;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage") // 기본 경로 유지
public class UserController {

    @GetMapping
    public String myPage(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        if (oauth2User != null) {
            // OAuth2 사용자 정보에서 필요한 속성 추출
            model.addAttribute("username", oauth2User.getAttribute("name"));
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("profileImageUrl", oauth2User.getAttribute("picture"));
        } else {
            // 로그인되지 않은 사용자에 대한 기본값
            model.addAttribute("username", "이름 없음");
            model.addAttribute("email", "이메일 없음");
            model.addAttribute("profileImageUrl", "/default-profile.png");
        }

        return "Mypage"; // Mypage.html
    }
}