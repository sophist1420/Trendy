package com.trendy.admin.product;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trendy.admin.review.AdminReview;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
@SuppressWarnings("unused")
public class AdminProductService {
	@Autowired
	private HttpSession session;
	@Autowired
	private AdminProductRepository productRepository;
	//@Autowired
    //private AdminOrderItemRepository orderItemRepository;

	// [목록 페이지]
	public List<AdminProduct> getAllProducts() {
	    List<AdminProduct> products = productRepository.findAllByOrderByProductIdDesc();
	    products.forEach(product -> {
	        System.out.println("Product ID: " + product.getProductId());
	    });
	    return products;
	}
	
	public List<AdminProduct> searchByProductId(Long productId) {
	    return productRepository.findByProductIdAsList(productId);
	}

	public List<AdminProduct> searchByBrand(String brand) {
	    return productRepository.findByBrand(AdminProduct.Brand.valueOf(brand));
	}
	 public List<String> getDistinctBrands() {
	        return productRepository.findDistinctBrands();
	    }

	    // 색상 목록 가져오기
	    public List<String> getDistinctColors() {
	        return productRepository.findDistinctColors();
	    }

	    // 사이즈 목록 가져오기
	    public List<String> getDistinctSizes() {
	        return productRepository.findDistinctSizes();
	    }

	    // 성별 목록 가져오기
	    public List<String> getDistinctGenders() {
	        return productRepository.findDistinctGenders();
	    }
	
	// 주문 상세 정보 조회
    public AdminProduct findByProductId(Long productId) {
        return productRepository.findByProductId(productId).orElse(null);
    }

	// [수정 페이지]
	// 게시물 조회
	public AdminProduct findById(Long productId) {
		return productRepository.findByProductId(productId).orElse(null);
	}
	// 게시물 수정 처리
	public String updateProduct(AdminProductDTO productDTO) {
		System.out.println("여기까지잘됨3");
	    AdminProduct product = productRepository.findByProductId(productDTO.getProductId()).orElse(null);
	    if (product == null) {
	        return "주문이 존재하지 않습니다.";
	    }
	    product.setProductName(productDTO.getProductName());
	    product.setPrice(productDTO.getPrice());
	    product.setModelId(productDTO.getModelId());
	    product.setQuantity(productDTO.getQuantity());
	    product.setBrand(AdminProduct.Brand.valueOf(productDTO.getBrand()));
	    product.setColor(productDTO.getColor());
	    product.setGender(AdminProduct.Gender.valueOf(productDTO.getGender()));
	    product.setSize(productDTO.getSize());
	    try {
	        productRepository.save(product);
	    } catch (Exception e) {
	        System.out.println("데이터 저장 실패: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return "성공적으로 수정되었습니다.";
	}

	// 게시물 삭제
	@Transactional
    public void deleteProduct(Long productId) {
        try {
        	productRepository.deleteByProductId(productId);
        } catch (Exception e) {
            throw new RuntimeException("삭제 중 오류 발생: " + e.getMessage());
        }
    }
	
	
	// 다중 게시물 삭제
	@Transactional
    public void deleteMultipleProducts(List<Long> productIds) {
        try {
        	productRepository.deleteAllByProductIdIn(productIds);
        } catch (Exception e) {
            throw new RuntimeException("다중 삭제 중 오류 발생: " + e.getMessage());
        }
    }
	
	 // 게시물 등록

	// 상품 등록
	public void registerProduct(AdminProductDTO productDTO) {
	    AdminProduct product = new AdminProduct();
	    product.setProductName(productDTO.getProductName());
	    product.setPrice(productDTO.getPrice());
	    product.setModelId(productDTO.getModelId());
	    product.setQuantity(productDTO.getQuantity());
	    product.setBrand(AdminProduct.Brand.valueOf(productDTO.getBrand()));
	    product.setColor(productDTO.getColor());
	    product.setGender(AdminProduct.Gender.valueOf(productDTO.getGender()));
	    product.setSize(productDTO.getSize());
	    try {
	        productRepository.save(product);
	    } catch (Exception e) {
	        System.out.println("데이터 저장 실패: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
/*
	public List<AdminProduct> searchAndSortProducts(String brand, String value, String sortOption) {
	    List<AdminProduct> products;

	    // 검색 처리
	    if (value != null && !value.isEmpty()) {
	        try {
	            Long productId = Long.parseLong(value);
	            products = productRepository.findByProductIdAsList(productId);
	        } catch (NumberFormatException e) {
	            products = List.of(); // 숫자로 변환할 수 없는 경우 빈 리스트 반환
	        }
	    } else if (brand != null && !brand.isEmpty()) {
	        products = productRepository.findByBrand(AdminProduct.Brand.valueOf(brand));
	    } else {
	        products = productRepository.findAll(); // 기본 전체 검색
	    }
	    return products;
	}
*/

	public List<AdminProduct> searchAndSortProducts(String brand, String value, String sortOption) {
	    List<AdminProduct> products = productRepository.findAll(); // 기본 전체 목록

	    // 검색 조건 처리
	    if (value != null && !value.isEmpty()) {
	        try {
	            Long productId = Long.parseLong(value);
	            products = productRepository.findByProductIdAsList(productId);
	        } catch (NumberFormatException e) {
	            return List.of(); // 잘못된 형식의 경우 빈 리스트 반환
	        }
	    } else if (brand != null && !brand.isEmpty()) {
	        products = productRepository.findByBrand(AdminProduct.Brand.valueOf(brand));
	    }

	    return products;
	}

}