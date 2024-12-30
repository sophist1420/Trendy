package com.trendy.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage/details") // 기본 경로 변경
public class MypageController {

    @GetMapping
    public String myPage(Model model) {
        model.addAttribute("username", "OAuth2 User");
        return "mypageDetails"; // mypageDetails.html
    }
}
