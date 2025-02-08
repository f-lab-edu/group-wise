package wj.flab.group_wise.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@Getter
public class ProductAttributeValueStock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_value_id")
    @NotNull
    private ProductAttributeValue productAttributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_stock_id")
    @NotNull
    private ProductStock productStock;

    protected ProductAttributeValueStock() {}

    protected ProductAttributeValueStock(ProductAttributeValue productAttributeValue, ProductStock productStock) {
        this.productAttributeValue = productAttributeValue;
        this.productStock = productStock;
    }
}
