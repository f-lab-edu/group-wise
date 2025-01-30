package wj.flab.group_wise.domain.product;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@Getter @Setter
public class ProductAttributeValue {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id")
    private ProductAttribute productAttribute;

    private String value;                           // 상품의 선택항목 값 (ex. 빨강, M 등)
    private int additionalPrice;                    // 추가금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_stock_id")
    private ProductStock productStock;

    @Embedded
    private BaseTimeEntity baseTimeEntity;

}
