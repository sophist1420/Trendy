package com.trendy.product;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;

    public LikeService(LikeRepository likeRepository, ProductRepository productRepository) {
        this.likeRepository = likeRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Map<String, Object> toggleLike(Long userId, Long productId) {
        Map<String, Object> result = new HashMap<>();
        
        if (userId == null) {
            result.put("success", false);
            return result;
        }

        Optional<Like> existingLike = likeRepository.findByUserIdAndProductId(userId, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        boolean isLiked;
        if (existingLike.isEmpty()) {
            // 좋아요 추가
            Like like = new Like(userId, productId);
            likeRepository.save(like);
            productRepository.incrementLikeCount(productId);
            isLiked = true;
        } else {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            productRepository.decrementLikeCount(productId);
            isLiked = false;
        }

        int updatedLikeCount = productRepository.getLikeCount(productId);
        
        result.put("liked", isLiked);
        result.put("likeCount", updatedLikeCount);
        return result;
    }
}

