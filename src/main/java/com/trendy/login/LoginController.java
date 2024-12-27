package com.trendy.login;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        model.addAttribute("user", oAuth2User.getAttributes());
        return "loginSuccess";
    }

    @GetMapping("/loginFailure")
    public String loginFailure(Model model) {
        model.addAttribute("errorMessage", "로그인에 실패했습니다. 다시 시도해주세요.");
        return "loginFailure";
    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        if (oAuth2User == null) {
            // 인증되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // OAuth2 사용자 속성 추가
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        System.out.println("OAuth2User Attributes: " + oAuth2User.getAttributes());

        model.addAttribute("user", attributes);

        return "mypage";
    }
}
