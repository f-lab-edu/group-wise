package wj.flab.group_wise.domain.product;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class ProductAttributeValue extends BaseTimeEntity {

    protected ProductAttributeValue(ProductAttribute productAttribute, String attributeValueName, int additionalPrice) {
        this.productAttribute = productAttribute;
        this.attributeValueName = attributeValueName;
        this.additionalPrice = additionalPrice;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id", nullable = false)
//    @NotNull
    private ProductAttribute productAttribute;      // 상품의 선택항목명 엔티티 (ex. 색상, 사이즈 등)

    @NotBlank
    private String attributeValueName;                  // 상품의 선택항목 값 (ex. 빨강, M 등)

    @Range(min = 0)
    private int additionalPrice;                    // 추가금액

    protected void unbindProductAttribute() {
        this.productAttribute = null;
    }

    protected void update(String attributeValue, int additionalPrice) {
        this.attributeValueName = attributeValue;
        this.additionalPrice = additionalPrice;
    }
}
