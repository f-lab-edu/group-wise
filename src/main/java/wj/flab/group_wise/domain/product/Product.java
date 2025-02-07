package wj.flab.group_wise.domain.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.dto.ProductAddDto.ProductAttributeDto;

@Entity
@Getter @Setter
public class Product extends BaseTimeEntity {

    public enum SaleStatus {
        SALE,       // 판매중
        SOLD_OUT,   // 품절
        DISCONTINUE // 단종
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Setter
    private String seller;                      // 판매사

    @NotBlank
    @Setter
    private String productName;                 // 상품명

    @Range(min = 0)
    @Setter
    private int basePrice;                      // 기준가(정가)

    @Range(min = 0)
    private int availableQuantity;              // 공구 가능한 수량

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;              // 판매상태

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true )
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    public static Product createProduct(
        String seller, String productName, int basePrice, int availableQuantity, List<ProductAttributeDto> productAttributeDtos) {
        Product product = new Product(seller, productName, basePrice, availableQuantity);

        productAttributeDtos.forEach(productAttributeDto -> {
            ProductAttribute productAttribute = new ProductAttribute(productAttributeDto.getAttributeName(), product); // 연관관계 주인 엔티티에 관계 설정
            productAttributeDto.getProductAttributeValues().forEach(productAttributeValueDto ->
                productAttribute.addValue( // 연관관계 주인 엔티티에 관계 설정
                    productAttributeValueDto.getAttributeValue(), productAttributeValueDto.getAdditionalPrice()));

            // this 엔티티에 관계 설정
            product.getProductAttributes().add(productAttribute);
        });

        return product;
    }

    protected Product() {
    }

    private Product(String seller, String productName, int basePrice, int availableQuantity) {
        this.seller = seller;
        this.productName = productName;
        this.basePrice = basePrice;
        this.availableQuantity = availableQuantity;
        setSaleStatusAsQuantity(availableQuantity);
    }

    private void setSaleStatusAsQuantity(int availableQuantity) {
        if (availableQuantity == 0) {
            this.saleStatus = SaleStatus.SOLD_OUT;
        } else {
            this.saleStatus = SaleStatus.SALE;
        }
    }

    public void changeAvailableQuantity(int quantity) {
        this.availableQuantity = quantity;
        setSaleStatusAsQuantity(availableQuantity);
    }

    public void increaseAvailableQuantity(int quantity) {
        this.availableQuantity += quantity;
        setSaleStatusAsQuantity(availableQuantity);
    }

    public void decreaseAvailableQuantity(int quantity) {
        if (availableQuantity - quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.availableQuantity -= quantity;
        setSaleStatusAsQuantity(availableQuantity);
    }

    public void changeSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
        switch (saleStatus) {
            case SALE:
                if (availableQuantity == 0) {
                    throw new IllegalArgumentException("재고가 부족합니다.");
                }
                break;
            case SOLD_OUT:
                if (availableQuantity > 0) {
                    throw new IllegalArgumentException("재고가 남아있습니다.");
                }
                break;
            case DISCONTINUE:
                break;
        }
    }


}
