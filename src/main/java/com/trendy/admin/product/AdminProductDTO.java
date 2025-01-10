package com.trendy.admin.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProductDTO {
	private Long productId;
    private String productName;
    private int price;
    private int quantity;
    private String imageUrl;
    private String imageDetailUrl1;
    private String imageDetailUrl2;
    private String imageDetailUrl3;
    private String modelId;
    private String warrantyGrade;
    private String region;
    private String brand;
    private String color;
    private String size;
    private String gender;
    private int likeCount;
    private String createdBy;
    private int stockQuantity;
    private boolean isDeleted;
}
