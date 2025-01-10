package com.trendy.mypage.resell;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trendy.login.CustomUserDetails;
import com.trendy.product.Product;
import com.trendy.product.ProductDTO;
import com.trendy.product.ProductOption;
import com.trendy.product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resell")
public class ResellProductController {
    
    private final ProductService productService;
    private final SalesDataService salesDataService;

    @PostMapping("/products/register")
    public ResponseEntity<?> registerResellProduct(
            @RequestBody ProductDTO productDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            // Product 엔티티 생성
            Product product = Product.builder()
                .name(productDTO.getName())
                .modelId(productDTO.getModelId())
                .price(productDTO.getPrice())
                .imageUrl(productDTO.getImageUrl())
                .imageDetailUrl1(productDTO.getImageDetailUrl1())
                .imageDetailUrl2(productDTO.getImageDetailUrl2())
                .imageDetailUrl3(productDTO.getImageDetailUrl3())
                .color(productDTO.getColor())
                .brand(Product.Brand.valueOf(productDTO.getBrand()))
                .gender(Product.Gender.valueOf(productDTO.getGender()))
                .productOptions(new ArrayList<>())
                .build();

            // ProductOption 생성
            ProductOption option = ProductOption.builder()
                .product(product)
                .size(ProductOption.Size.fromValue(productDTO.getProductOptions().get(0).getSize()))
                .isAvailable(true)
                .stockQuantity(1)
                .build();

            product.getProductOptions().add(option);

            // Product 저장
            Product savedProduct = productService.saveProduct(product);

            // SalesData 생성 및 저장
            SalesData salesData = SalesData.builder()
                .modelId(savedProduct.getModelId())
                .warrantyGrade(null)  // warrantyGrade를 null로 설정
                .productOption(option)
                .userId(userDetails.getUserId())
                .quantity(1)
                .totalPrice(savedProduct.getPrice())
                .saleDate(LocalDate.now())
                .build();

            salesDataService.saveSalesData(salesData);
            
            // 응답 DTO 생성
            ProductDTO responseDTO = ProductDTO.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .modelId(savedProduct.getModelId())
                .price(savedProduct.getPrice())
                .imageUrl(savedProduct.getImageUrl())
                .imageDetailUrl1(savedProduct.getImageDetailUrl1())
                .imageDetailUrl2(savedProduct.getImageDetailUrl2())
                .imageDetailUrl3(savedProduct.getImageDetailUrl3())
                .color(savedProduct.getColor())
                .brand(savedProduct.getBrand().name())
                .gender(savedProduct.getGender().name())
                .build();

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteResellProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            // 상품과 관련된 판매 데이터 조회
            SalesData salesData = salesDataService.findByProductOptionProductIdAndUserId(productId, userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 상품의 판매 데이터를 찾을 수 없습니다."));

            // 판매 데이터 삭제
            salesDataService.deleteSalesData(salesData);

            // 상품 삭제
            productService.deleteProduct(productId);

            return ResponseEntity.ok().body("상품이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
