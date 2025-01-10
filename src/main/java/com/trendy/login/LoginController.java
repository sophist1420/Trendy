package com.trendy.login;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String loginSuccess() {
        System.out.println("[DEBUG] Redirected to loginSuccess page.");
        return "loginSuccess";
    }

    @GetMapping("/loginFailure")
    public String loginFailure(Model model) {
        model.addAttribute("errorMessage", "로그인에 실패했습니다. 다시 시도해주세요.");
        return "loginFailure";
    }

    @GetMapping("/api/auth/status")
    public ResponseEntity<?> getAuthStatus(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok(Map.of(
                "isAuthenticated", true,
                "username", userDetails.getUsername()
            ));
        }
        return ResponseEntity.ok(Map.of("isAuthenticated", false));
    }

    @GetMapping("/api/auth/oauth2/result")
    public ResponseEntity<?> getOAuth2Result() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "redirect", "http://localhost:3000/loginSuccess"
            ));
        }
        return ResponseEntity.ok(Map.of(
            "success", false,
            "redirect", "http://localhost:3000/loginFailure"
        ));
    }
}