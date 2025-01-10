package com.trendy.review;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.trendy.login.CustomUserDetails;
import com.trendy.order.Order;
import com.trendy.order.OrderRepository;
import com.trendy.product.Product;
import com.trendy.product.ProductService;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String showReviewForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();
        
        // 완료된 주문 조회
        List<Order> completedOrders = orderRepository.findByUserIdAndStatus(userId, Order.OrderStatus.completed);
        List<Product> availableProducts = new ArrayList<>();
        
        // 첫 번째 주문의 상품 정보를 기본으로 표시
        if (!completedOrders.isEmpty()) {
            Order firstOrder = completedOrders.get(0);
            Product defaultProduct = productService.getProductById(firstOrder.getProductId());
            model.addAttribute("product", defaultProduct);  // 기본 상품 정보 추가
            
            // 리뷰 작성 가능한 상품 목록
            for (Order order : completedOrders) {
                Product product = productService.getProductById(order.getProductId());
                if (product != null) {
                    availableProducts.add(product);
                }
            }
        }
        
        List<Review> reviews = reviewRepository.findByUserId(userId);
        
        model.addAttribute("availableProducts", availableProducts);
        model.addAttribute("reviews", reviews);
        model.addAttribute("userId", userId);
        
        return "review";
    }

    @PostMapping
    public String saveReview(
            @RequestParam("productId") Long productId,
            @RequestParam("content") String content,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {
        // 이미지 크기 체크 (예: 5MB 제한)
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 5 * 1024 * 1024) { // 5MB
                throw new RuntimeException("이미지 크기는 5MB를 초과할 수 없습니다.");
            }
        }
        
        Review review = new Review();
        review.setProductId(productId);
        review.setContent(content);
        review.setUserId(userDetails.getUserId());

        if (!image.isEmpty()) {
            review.setImage(image.getBytes());
        }

        reviewRepository.save(review);
        return "redirect:/mypage";
    }

    // 리뷰 수정 폼 표시
    @GetMapping("/edit/{reviewId}")
    public String showEditForm(@PathVariable Long reviewId, 
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        Review review = reviewRepository.findByReviewIdAndUserId(reviewId, userDetails.getUserId())
            .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없거나 권한이 없습니다."));
        model.addAttribute("review", review);
        return "review-edit";
    }

    // 리뷰 수정 처리
    @PostMapping("/edit/{reviewId}")
    public String updateReview(@PathVariable("reviewId") Long reviewId,
                             @RequestParam("content") String content,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Review review = reviewRepository.findByReviewIdAndUserId(reviewId, userDetails.getUserId())
            .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없거나 권한이 없습니다."));
        
        review.setContent(content);
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 5 * 1024 * 1024) { // 5MB 제한
                throw new RuntimeException("이미지 크기는 5MB를 초과할 수 없습니다.");
            }
            review.setImage(image.getBytes());
        }
        
        reviewRepository.save(review);
        return "redirect:/review";
    }

    // 리뷰 삭제
    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") Long reviewId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Review review = reviewRepository.findByReviewIdAndUserId(reviewId, userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없거나 권한이 없습니다."));
            
            reviewRepository.delete(review);
            return "redirect:/review";
        } catch (Exception e) {
            throw new RuntimeException("리뷰 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}