package com.trendy.review;

import com.trendy.login.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.io.IOException;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping
    public String showReviewForm(@RequestParam Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "review";
    }

    @PostMapping
    public String saveReview(
            @RequestParam("productId") Long productId,
            @RequestParam("content") String content,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {
        // 리뷰 엔티티 생성 및 데이터 설정
        Review review = new Review();
        review.setProductId(productId);
        review.setContent(content);
        review.setUserId(userDetails.getUserId());

        if (!image.isEmpty()) {
            review.setImage(image.getBytes());
        }

        // 리뷰 저장
        reviewRepository.save(review);
        return "redirect:/mypage";
    }
}
