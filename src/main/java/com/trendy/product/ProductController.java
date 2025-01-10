package com.trendy.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.trendy.login.CustomUserDetails;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    
    

    
    // product entity post
    @PostMapping("/products")
    @ResponseBody
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            Product createdProduct = productService.createProduct(productDTO);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // 상품 필터링 API
    @PostMapping("/products/filter")
    @ResponseBody
    public List<ProductDTO> filterProducts(@RequestBody Map<String, List<String>> filters) {
        return productService.filterProducts(filters);
    }
    
    
    @GetMapping("/products")
    @ResponseBody
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }
    
    
    // 좋아요 토글
    @PostMapping("/products/{productId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
            
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> result = productService.toggleLike(userDetails.getUserId(), productId);
        return ResponseEntity.ok(result);
    }



    // 검색
    @GetMapping("/products/search")
    public List<ProductDTO> searchProductsByName(@RequestParam("name") String name) {
        return productService.searchProductsByName(name);
    }

    
    // 상품 옵션 ID 조회 API 추가
    @GetMapping("/products/{productId}/option")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductOption(
            @PathVariable("productId") Long productId,
            @RequestParam("size") String size) {
        try {
            // ProductOption 조회
            List<ProductOption> options = productService.getProductOptions(productId);
            
            // 해당 사이즈의 옵션 찾기
            ProductOption option = options.stream()
                .filter(o -> o.getSize().getValue().equals(size))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 사이즈의 상품이 없습니다."));

            Map<String, Object> response = new HashMap<>();
            response.put("optionId", option.getId());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 상품 삭제
    @PostMapping("/products/{productId}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "상품이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "상품 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}