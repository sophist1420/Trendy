package com.trendy.admin.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Products")
public class AdminProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private int price;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_detail_url1")
    private String imageDetailUrl1;

    @Column(name = "image_detail_url2")
    private String imageDetailUrl2;

    @Column(name = "image_detail_url3")
    private String imageDetailUrl3;

    @Column(name = "model_id", nullable = false)
    private String modelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "warranty_grade")
    private WarrantyGrade warrantyGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "brand", nullable = false)
    private Brand brand;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "like_count", columnDefinition = "int default 0")
    private int likeCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by")
    private CreatedBy createdBy;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // Enum Classes
    public enum WarrantyGrade { A, B, C }
    public enum Region { Mapo, Gangnam, Jamsil }
    public enum Brand { Nike, Adidas, Asics, New_Balance, Reebok, Others }
    //public enum Size { SIZE_230, SIZE_240, SIZE_250, SIZE_260, SIZE_270, SIZE_280, SIZE_290 }
    public enum Gender { male, female }
    public enum CreatedBy { admin, seller }
    /*
    public enum Size {
        SIZE_230("230"),
        SIZE_240("240"),
        SIZE_250("250"),
        SIZE_260("260"),
        SIZE_270("270"),
        SIZE_280("280"),
        SIZE_290("290");

        private final String value;

        Size(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Size fromValue(String value) {
            for (Size size : Size.values()) {
                if (size.value.equals(value)) {
                    return size;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
*/
}
