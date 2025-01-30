package wj.flab.group_wise.domain.product;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@Getter @Setter
public class Product {

    public enum SaleStatus {
        SALE,       // 판매중
        SOLD_OUT,   // 품절
        DISCONTINUE // 단종
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                        // 상품명
    private int basePrice;                      // 기준가(정가)

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;              // 판매상태

    private String seller;                      // 판매사
    private int availableQuantity;              // 공구 가능한 수량
//    private int deliveryFee;                    // 배송비

//    private String description;                 // 상품 설명
//    private String thumbnailUrl;                // 썸네일 URL

    @Embedded
    private BaseTimeEntity baseTimeEntity;

    protected Product() {
    }

    public Product(String name, int basePrice, SaleStatus saleStatus, String seller, int availableQuantity) {
        this.name = name;
        this.basePrice = basePrice;
        this.saleStatus = saleStatus;
        this.seller = seller;
        this.availableQuantity = availableQuantity;
    }
}
