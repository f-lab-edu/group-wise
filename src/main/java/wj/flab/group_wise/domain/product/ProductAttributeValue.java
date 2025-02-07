package wj.flab.group_wise.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@Getter
@EqualsAndHashCode(of = {"productAttribute", "attributeValue"})
public class ProductAttributeValue extends BaseTimeEntity {

    protected ProductAttributeValue() {}

    protected ProductAttributeValue(ProductAttribute productAttribute, String attributeValue, int additionalPrice) {
        this.productAttribute = productAttribute;
        this.attributeValue = attributeValue;
        this.additionalPrice = additionalPrice;
        // todo setProductStock();
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id")
    @NotNull
    private ProductAttribute productAttribute;      // 상품의 선택항목명 엔티티 (ex. 색상, 사이즈 등)

    @NotBlank
    private String attributeValue;                  // 상품의 선택항목 값 (ex. 빨강, M 등)

    @Range(min = 0)
    private int additionalPrice;                    // 추가금액

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_stock_id")
//    private ProductStock productStock;

    protected void unbindProductAttribute() {
        this.productAttribute = null;
    }

    protected void update(String attributeValue, int additionalPrice) {
        this.attributeValue = attributeValue;
        this.additionalPrice = additionalPrice;
        // todo setProductStock();
    }

    // todo
//    private void setProductStock() {
//        this.productStock = new ProductStock();
//    }

}
