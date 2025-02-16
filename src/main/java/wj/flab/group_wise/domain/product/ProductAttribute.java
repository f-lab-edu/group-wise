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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.util.ListUtils.ContainerOfValues;

@Entity
@Getter
public class ProductAttribute extends BaseTimeEntity implements ContainerOfValues<ProductAttributeValue> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Setter
    private String attributeName;                // 상품의 선택항목명 (ex. 색상, 사이즈 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;

    @OneToMany(
        mappedBy = "productAttribute",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true )
    private List<ProductAttributeValue> values = new ArrayList<>();    // 옵션목록

    protected ProductAttribute() {}

    public ProductAttribute(String attributeName, Product product) {
        this.attributeName = attributeName;
        this.product = product;
    }

    public ProductAttribute(String attributeName, Product product, List<ProductAttributeValue> values) {
        this.attributeName = attributeName;
        this.product = product;
        this.values = values;
    }

    // 옵션값 추가 (양방향 편의 메서드)
    public void addValue(String attributeValue, int additionalPrice) {
        validateUniqueValue(attributeValue);

        // 옵션값 생성 (+ 연관관계 주인 엔티티에 관계 설정)
        ProductAttributeValue value = new ProductAttributeValue(this, attributeValue, additionalPrice);
        values.add(value);
    }

    // 옵션값 삭제 (양방향 편의 메서드)
    public void removeValue(ProductAttributeValue value) {
        values.remove(value);
        value.unbindProductAttribute(); // 연관관계 주인 엔티티에 관계 설정 해제
    }

    // 옵션값 변경 (양방향 편의 메서드)
    public void updateValue(String attributeValue, int additionalPrice) {
        validatePresenceOfValue(attributeValue);

        values.stream().filter(v -> v.getAttributeValue().equals(attributeValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."))
            .update(attributeValue, additionalPrice);
    }

    private void validateUniqueValue(String attributeValue) {
        boolean hasSameAttributeValue = values.stream().anyMatch(v -> v.getAttributeValue().equals(attributeValue));
        if (hasSameAttributeValue) {
            throw new IllegalArgumentException("이미 존재하는 옵션입니다.");
        }
    }

    private void validatePresenceOfValue(String attributeValue) {
        boolean hasSameAttributeValue = values.stream().anyMatch(v -> v.getAttributeValue().equals(attributeValue));
        if ( ! hasSameAttributeValue ) {
            throw new IllegalArgumentException("존재하지 않는 옵션입니다.");
        }
    }


}
