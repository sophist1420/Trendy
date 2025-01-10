package com.trendy.order;

import com.trendy.order.Order.*;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Long optionId;
    private String modelId;
    private Integer quantity;
    private Integer price;
    private String shippingAddress;
    private PickupLocation pickupLocation;
    private OrderStatus status;
}