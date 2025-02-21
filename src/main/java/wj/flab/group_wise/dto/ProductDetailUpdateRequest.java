package wj.flab.group_wise.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;

/**
 * 공동구매 진행상태가 아닌 상품의 수정을 위한 DTO
 */
public record ProductDetailUpdateRequest(@NotNull Long productId,
                                         @NotBlank String seller,
                                         @NotBlank String productName,
                                         @Range(min = 0) int basePrice,
                                         @Enumerated(EnumType.STRING) SaleStatus saleStatus,
                                         List<AttributeCreateRequest> newAttributes,
                                         List<AttributeUpdateRequest> updateAttributes,
                                         List<ProductAttributeDeleteRequest> deleteAttributesIds) {

    /**
     * 상품의 선택항목명 (ex. 색상, 사이즈 등)
     */
    public record AttributeUpdateRequest(@NotNull Long productAttributeId,
                                         @NotBlank String attributeName,
                                         List<AttributeValueCreateRequest> newAttributeValues,
                                         List<AttributeValueUpdateRequest> updateAttributeValues,
                                         List<AttributeValueDeleteRequest> deleteAttributeValuesIds) {

        /**
         * 상품의 선택항목 값 (ex. 빨강, M 등)
         */
        public record AttributeValueUpdateRequest(@NotNull Long productAttributeValueId,
                                                  @NotBlank String attributeValueName,
                                                  @Range(min = 0) int additionalPrice) {}

        public record AttributeValueDeleteRequest(@NotNull Long productAttributeValueId) {}
    }

    public record ProductAttributeDeleteRequest(@NotNull Long productAttributeId) {}
}
