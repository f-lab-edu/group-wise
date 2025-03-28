package wj.flab.group_wise.domain.product;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.dto.product.response.ProductStockResponse;
import wj.flab.group_wise.dto.product.response.ProductStockResponse.ProductAttributeValueResponse;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ProductStock extends BaseTimeEntity implements Purchasable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(PROTECTED)
    private Long id;

    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    @Getter(PROTECTED)
    private Product product;

    @OneToMany(
        mappedBy = "productStock",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @Getter(PROTECTED)
    private List<ProductAttributeValueStock> values = new ArrayList<>(); // 상품 선택 항목에 대해 선택된 값

    protected ProductStock(Product product) {
        this.product = product;
    }

    protected ProductStock(Product product, List<ProductAttributeValue> values) {
        this.product = product;
        initializeStockValues(values);
    }

    private void initializeStockValues(List<ProductAttributeValue> values) {
        values.forEach(v -> this.values.add(new ProductAttributeValueStock(v, this)));
    }

    @Override
    public int getPrice() {
        return product.getBasePrice()
            + values.stream()
            .mapToInt(v -> v.getProductAttributeValue().getAdditionalPrice())
            .sum();
    }

    @Override
    public int getStockQuantity() {
        return stockQuantity == null ? 0 : stockQuantity;
    }

    protected void decreaseStockQuantity(int quantity) {
        if (stockQuantity - quantity < 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        stockQuantity -= quantity;
    }

    protected void addStockQuantity(int quantity) {
        this.stockQuantity = getStockQuantity() + quantity;
    }

    protected boolean hasStockQuantitySet() {
        return stockQuantity != null;
    }

    protected void setStockQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
        this.stockQuantity = quantity;
    }

    protected ProductStockResponse toResponse() {
        return new ProductStockResponse(
            product.getId(),
            id,
            getStockQuantity(),
            getPrice(),
            getAttributeValuesDto(),
            getCreatedDate(),
            getModifiedDate()
        );
    }

    private List<ProductAttributeValueResponse> getAttributeValuesDto() {
        return this.values.stream()
            .map(ProductAttributeValueStock::getProductAttributeValue)
            .map(value -> new ProductAttributeValueResponse(
                value.getProductAttribute().getId(),
                value.getProductAttribute().getAttributeName(),
                value.getId(),
                value.getAttributeValueName(),
                value.getAdditionalPrice()
            ))
            .toList();
    }
}
