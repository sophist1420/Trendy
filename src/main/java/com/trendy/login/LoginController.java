package com.trendy.login;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String loginSuccess() {
        System.out.println("[DEBUG] Redirected to loginSuccess page.");
        return "loginSuccess"; // templates/loginSuccess.html
    }

    @GetMapping("/loginFailure")
    public String loginFailure(Model model) {
        model.addAttribute("errorMessage", "로그인에 실패했습니다. 다시 시도해주세요.");
        return "loginFailure";
    }

}
