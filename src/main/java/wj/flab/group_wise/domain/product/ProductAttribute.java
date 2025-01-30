package wj.flab.group_wise.domain.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
public class ProductAttribute extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String attributeName;                // 상품의 선택항목명 (ex. 색상, 사이즈 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(
        mappedBy = "productAttribute",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true )
    private List<ProductAttributeValue> values;    // 옵션목록

}
