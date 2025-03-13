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
import wj.flab.group_wise.domain.exception.AlreadyExistsException;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.dto.product.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueDeleteRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueUpdateRequest;
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

    public void updateAttributeName(@NotBlank String attrName) {
        this.attributeName = attrName;
    }

    public void appendValues(List<AttributeValueCreateRequest> valuesToCreate) {
        valuesToCreate.forEach(v -> {
            checkHasAlreadySameNameOfAttrValue(v.attributeValueName());
            values.add(new ProductAttributeValue(this, v.attributeValueName(), v.additionalPrice()));
        });
    }

    public boolean updateValues(List<AttributeValueUpdateRequest> valuesToUpdate) {
        boolean differenceFoundInAdditionalPrice = false;

        for (AttributeValueUpdateRequest valueToUpdate : valuesToUpdate) {
            ProductAttributeValue value = getProductAttributeValue(valueToUpdate.productAttributeValueId());
            if (value.getAdditionalPrice() != valueToUpdate.additionalPrice()) {
                differenceFoundInAdditionalPrice = true;
            }
            value.update(valueToUpdate.attributeValueName(), valueToUpdate.additionalPrice());
        }

        return differenceFoundInAdditionalPrice;
    }

    public void removeValues(List<AttributeValueDeleteRequest> valuesToRemove) {
        valuesToRemove.forEach(valueToRemove -> {
            ProductAttributeValue value = getProductAttributeValue(valueToRemove.productAttributeValueId());
            values.remove(value);
            value.unbindProductAttribute();
        });
    }

    private ProductAttributeValue getProductAttributeValue(Long productAttributeValueId) {
        return values.stream()
            .filter(v -> v.getId().equals(productAttributeValueId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.PRODUCT_ATTRIBUTE_VALUE, productAttributeValueId));
    }

    private void checkHasAlreadySameNameOfAttrValue(String attributeValueName) {
        boolean hasSameAttributeValue = values.stream().anyMatch(
            v -> v.getAttributeValueName().equals(attributeValueName));

        if (hasSameAttributeValue) {
            throw new AlreadyExistsException(TargetEntity.PRODUCT_ATTRIBUTE, "이미 존재하는 속성값입니다. (" + attributeValueName + ")");
        }
    }
}
