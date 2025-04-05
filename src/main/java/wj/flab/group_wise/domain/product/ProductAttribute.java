package wj.flab.group_wise.domain.product;

import static lombok.AccessLevel.PRIVATE;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.exception.AlreadyExistsException;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueUpdateRequest;
import wj.flab.group_wise.util.ListUtils.ContainerOfValues;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter(/*PROTECTED*/)
public class ProductAttribute extends BaseTimeEntity implements ContainerOfValues<ProductAttributeValue> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(PRIVATE)
    private Long id;

    @NotBlank
    private String attributeName;                // 상품의 선택항목명 (ex. 색상, 사이즈 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;

    @OneToMany(
        mappedBy = "productAttribute",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<ProductAttributeValue> values = new ArrayList<>();    // 옵션목록

    @Override
    // todo 어떻게 적절하게 보호할까
    public List<ProductAttributeValue> getValues() {
        return values;
    }

    protected ProductAttribute(String attributeName, Product product) {
        this.attributeName = attributeName;
        this.product = product;
    }

    protected void updateAttributeName(@NotBlank String attrName) {
        this.attributeName = attrName;
    }

    protected void appendValues(List<AttributeValueCreateRequest> valuesToCreate) {
        valuesToCreate.forEach(v -> {
            checkHasAlreadySameNameOfAttrValue(v.attributeValueName());
            values.add(new ProductAttributeValue(this, v.attributeValueName(), v.additionalPrice()));
        });
    }

    protected boolean updateValues(List<AttributeValueUpdateRequest> valuesToUpdate) {
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

    protected void removeValues(List<AttributeValueDeleteRequest> valuesToRemove) {
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
