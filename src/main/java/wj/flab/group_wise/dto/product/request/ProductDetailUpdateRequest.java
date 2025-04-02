package wj.flab.group_wise.dto.product.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;

/**
 * 공동구매 진행상태가 아닌 상품의 수정을 위한 DTO
 */
public record ProductDetailUpdateRequest(String seller,
                                         String productName,
                                         Integer basePrice,
                                         SaleStatus saleStatus,
                                         List<AttributeCreateRequest> newAttributes,
                                         List<AttributeUpdateRequest> updateAttributes,
                                         List<AttributeDeleteRequest> deleteAttributesIds) {

    /**
     * 상품의 선택항목명 (ex. 색상, 사이즈 등)
     */
    public record AttributeUpdateRequest(@NotNull Long productAttributeId,
                                         String attributeName,
                                         List<AttributeValueCreateRequest> newAttributeValues,
                                         List<AttributeValueUpdateRequest> updateAttributeValues,
                                         List<AttributeValueDeleteRequest> deleteAttributeValuesIds) {

        /**
         * 상품의 선택항목 값 (ex. 빨강, M 등)
         */
        public record AttributeValueUpdateRequest(@NotNull Long productAttributeValueId,
                                                  String attributeValueName,
                                                  Integer additionalPrice) {}

        // todo List<Integer>로 변경
        public record AttributeValueDeleteRequest(@NotNull Long productAttributeValueId) {}
    }

    // todo List<Integer>로 변경
    public record AttributeDeleteRequest(@NotNull Long productAttributeId) {}
}
