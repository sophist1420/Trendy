package com.trendy.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {
	@Autowired
	private HttpSession session;
    @Autowired
    private AdminProductService productService;
    @Autowired
    private AdminProductRepository productRepository;

    // [목록 페이지]
    @GetMapping
    public String getProductPage(Model model) {
        List<AdminProduct> products = productService.getAllProducts();

        if (products == null || products.isEmpty()) {
            System.out.println("주문 목록이 비어 있습니다."); // 디버깅 로그
        }
        
        model.addAttribute("products", products);
        return "AdProductPage"; // 목록 페이지 템플릿
    }
    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("product", new AdminProductDTO());
        model.addAttribute("brands", productService.getDistinctBrands());
        model.addAttribute("colors", productService.getDistinctColors());
        model.addAttribute("sizes", productService.getDistinctSizes());
        model.addAttribute("genders", productService.getDistinctGenders());
        return "AdProductRegister"; // 등록 페이지 템플릿
    }
    // [등록 처리]
    @PostMapping("/register")
    public String registerProduct(@ModelAttribute AdminProductDTO productDTO, RedirectAttributes ra) {
        try {
        	productService.registerProduct(productDTO);
            ra.addFlashAttribute("msg", "주문이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "주문 등록 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/product";
    }

    // [수정 페이지 이동]
    @GetMapping("/modify")
    public String getModifyPage(@RequestParam("productId") Long productId, Model model) {
        AdminProduct product = productService.findByProductId(productId);
        if (product == null) {
            return "redirect:/admin/product"; // 주문이 없을 경우 목록으로 리다이렉트
        }
        model.addAttribute("product", product);
        model.addAttribute("brands", productService.getDistinctBrands());
        model.addAttribute("colors", productService.getDistinctColors());
        model.addAttribute("sizes", productService.getDistinctSizes());
        model.addAttribute("genders", productService.getDistinctGenders());
        return "AdProductModify"; // 수정 페이지 템플릿
    }

    @PostMapping("/modify")
    public String updateProduct(@ModelAttribute AdminProductDTO productDTO, RedirectAttributes ra) {
    	System.out.println("여기까지잘됨1");
    	try {
    		System.out.println("여기까지잘됨2");
            String msg = productService.updateProduct(productDTO);
            ra.addFlashAttribute("msg", msg);
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "주문 수정 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/product"; // 수정 후 목록 페이지로 리다이렉트
    }

    // [삭제]
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("productId") Long productId, RedirectAttributes ra) {
        try {
        	productService.deleteProduct(productId);
            ra.addFlashAttribute("msg", "주문이 삭제되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "주문 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "redirect:/admin/product";
    }

    
    // [선택 삭제]
    @PostMapping("/delete-multiple")
    public String deleteMultipleProducts(@RequestParam(value = "productIds", required = false) List<Long> productIds, RedirectAttributes ra) {
        if (productIds == null || productIds.isEmpty()) {
            ra.addFlashAttribute("msg", "삭제할 주문을 선택하세요.");
            return "redirect:/admin/product";
        }

        try {
        	productService.deleteMultipleProducts(productIds);
            ra.addFlashAttribute("msg", "선택된 주문이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "주문 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        
        return "redirect:/admin/product";
    }
    /*
    @GetMapping("/search")
    public String searchProducts(
        @RequestParam(value = "value", required = false) String value,
        @RequestParam(value = "brand", required = false) String brand,
        Model model
    ) {
        List<AdminProduct> products;

        // 검색 조건
        if (value != null && !value.isEmpty()) {
            try {
                Long productId = Long.parseLong(value);
                products = productService.searchByProductId(productId);
            } catch (NumberFormatException e) {
                products = List.of(); // 숫자가 아닌 값이 입력된 경우 빈 리스트 반환
            }
        } else if (brand != null && !brand.isEmpty()) {
            products = productService.searchByBrand(brand);
        } else {
            products = productService.getAllProducts(); // 전체 목록 반환
        }

        model.addAttribute("products", products);
        model.addAttribute("brands", productService.getDistinctBrands());
        return "AdProductPage";
    }
    */
    @GetMapping("/search")
    public String searchProducts(
        @RequestParam(value = "value", required = false) String value,
        @RequestParam(value = "brand", required = false) String brand,
        Model model
    ) {
        List<AdminProduct> products;

        // 검색 조건 처리
        if (value != null && !value.isEmpty()) {
            try {
                Long productId = Long.parseLong(value);
                products = productService.searchByProductId(productId);
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Invalid product ID format.");
                products = List.of(); // 잘못된 형식의 경우 빈 리스트 반환
            }
        } else if (brand != null && !brand.isEmpty()) {
            products = productService.searchByBrand(brand);
        } else {
            products = productService.getAllProducts(); // 전체 목록 반환
        }

        // 브랜드 목록은 항상 제공
        model.addAttribute("brands", productService.getDistinctBrands());
        model.addAttribute("products", products);

        return "AdProductPage";
    }
}
