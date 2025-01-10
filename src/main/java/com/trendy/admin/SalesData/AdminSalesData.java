//package com.trendy.admin.SalesData;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.sql.Timestamp;
//
//@Entity
//@Getter
//@Setter
//@Table(name = "Sales_Management")
//public class AdminSalesData {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "sale_id")
//    private Long saleId;
//
//    @Column(name = "seller_id", nullable = false)
//    private Long sellerId;
//
//    @Column(name = "product_id", nullable = false)
//    private Long productId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private Status status = Status.on_sale;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "settlement_status", nullable = false)
//    private SettlementStatus settlementStatus = SettlementStatus.pending;
//
//    @Column(name = "created_at", updatable = false)
//    private Timestamp createdAt;
//
//    @Column(name = "updated_at")
//    private Timestamp updatedAt;
//
//    public enum Status {
//        on_sale, sold_out
//    }
//
//    public enum SettlementStatus {
//        pending, completed
//    }
//
//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = new Timestamp(System.currentTimeMillis());
//        this.updatedAt = new Timestamp(System.currentTimeMillis());
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedAt = new Timestamp(System.currentTimeMillis());
//    }
//}
