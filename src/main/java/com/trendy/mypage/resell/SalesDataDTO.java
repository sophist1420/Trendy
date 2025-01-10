package com.trendy.mypage.resell;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.trendy.mypage.resell.SalesData.WarrantyGrade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesDataDTO {
    private Long id;
    private String modelId;
    private WarrantyGrade warrantyGrade;
    private Long optionId;      // ProductOption의 ID
    private Long userId;        // User의 ID
    private Integer quantity;
    private Integer totalPrice;
    private LocalDate saleDate;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 메서드
    public static SalesDataDTO fromEntity(SalesData salesData) {
        return SalesDataDTO.builder()
                .id(salesData.getId())
                .modelId(salesData.getModelId())
                .warrantyGrade(salesData.getWarrantyGrade())
                .optionId(salesData.getProductOption().getId())
                .userId(salesData.getUserId())
                .quantity(salesData.getQuantity())
                .totalPrice(salesData.getTotalPrice())
                .saleDate(salesData.getSaleDate())
                .createdAt(salesData.getCreatedAt())
                .build();
    }
}