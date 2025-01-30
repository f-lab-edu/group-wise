package wj.flab.group_wise.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Getter @Setter
public class Product {

    public enum SaleStatus {
        SALE,       // 판매중
        SOLD_OUT,   // 품절
        DISCONTINUE // 단종
    }

    enum ProductStatus {
        SALE,       // 판매중
        SOLD_OUT,   // 품절
        DISCONTINUE // 단종
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                        // 상품명
    private int price;                          // 기준가(정가)

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;        // 상품상태

    @OneToMany(mappedBy = "product")
    private List<ProductCategory> categories;   // 카테고리
    private String seller;                      // 제조사
    private int availableQuantity;              // 공구 가능한 수량
    private int deliveryFee;                    // 배송비

//    private String description;                 // 상품 설명
//    private String thumbnailUrl;                // 썸네일 URL

    @Embedded
    private BaseTimeEntity baseTimeEntity;

    protected Product() {
    }

    public Product(String name, int basePrice, SaleStatus saleStatus, String seller, int availableQuantity, int deliveryFee) {
        this.name = name;
        this.basePrice = basePrice;
        this.saleStatus = saleStatus;
        this.seller = seller;
        this.availableQuantity = availableQuantity;
        this.deliveryFee = deliveryFee;
    }
}
