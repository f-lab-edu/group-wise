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
    private List<ProductAttribute> productAttributes = new ArrayList<>();  // 상품의 선택항목

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true )
    private List<ProductStock> productStocks = new ArrayList<>();           // 상품의 선택항목 조합에 따른 재고

    public static Product createProduct(String seller, String productName, int basePrice, int availableQuantity) {
        return new Product(seller, productName, basePrice, availableQuantity);
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

    public void addProductAttribute(ProductAttribute productAttribute) {
        productAttributes.add(productAttribute);
    }

    public void generateProductStocks() {
        if (getProductAttributes().isEmpty()) {
            throw new IllegalArgumentException("상품 선택 항목(productAttributes)이 존재하지 않습니다.");
        }
        generateProductStocks(productAttributes, 0, new ArrayList<>());
    }

    private void generateProductStocks(
        List<ProductAttribute> attributes,                      // 상품의 선택 항목 리스트 (색상, 사이즈, ...)
        int currentAttrIdx,                                     // 현재 선택 항목 인덱스
        List<ProductAttributeValueStock> currentCombination) {  // 현재까지 선택한 조합

        // 모든 속성에 대한 선택이 완료되면
        if (currentAttrIdx == attributes.size()) {
            // 새로운 ProductStock 생성
            ProductStock newStock = new ProductStock(this);

            // 현재까지의 조합을 새 ProductStock에 추가
            for (ProductAttributeValueStock valueStock : currentCombination) {
                newStock.getValues().add(new ProductAttributeValueStock(
                    valueStock.getProductAttributeValue(),
                    newStock
                ));
            }

            // Product의 재고 리스트에 추가
            this.productStocks.add(newStock);
            return;
        }

        ProductAttribute currentAttr = attributes.get(currentAttrIdx);
        for (ProductAttributeValue value : currentAttr.getValues()) {
            // 현재 값으로 ProductAttributeValueStock 생성 (임시)
            ProductAttributeValueStock valueStock = new ProductAttributeValueStock(value, null);
            currentCombination.add(valueStock);

            // 다음 속성으로 진행
            generateProductStocks(attributes, currentAttrIdx + 1, currentCombination);

            // 백트래킹: 마지막 선택 제거
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

}
