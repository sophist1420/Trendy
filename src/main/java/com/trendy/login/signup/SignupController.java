package com.trendy.login.signup;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.trendy.login.User;
import com.trendy.login.UserRepository;

@Controller
public class SignupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 페이지 요청
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") User user, Model model) {
        // 사용자 입력값 직접 검증
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "아이디는 필수 입력값입니다.");
            return "signup";
        }

        if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            model.addAttribute("error", "아이디는 3자 이상, 20자 이하로 입력해주세요.");
            return "signup";
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "signup";
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "이메일은 필수 입력값입니다.");
            return "signup";
        }

        if (!user.getEmail().contains("@")) {
            model.addAttribute("error", "유효한 이메일 주소를 입력해주세요.");
            return "signup";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "signup";
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "비밀번호는 필수 입력값입니다.");
            return "signup";
        }

        if (user.getPassword().length() < 8) {
            model.addAttribute("error", "비밀번호는 최소 8자 이상이어야 합니다.");
            return "signup";
        }

        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    // 사용자 이름 중복 확인 API
    @GetMapping("/check-username")
    @ResponseBody
    public String checkUsername(@RequestParam("username") String username) {
        if (userRepository.findByUsername(username) != null) {
            return "duplicate";
        }
        return "available";
    }

}
