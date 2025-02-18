package wj.flab.group_wise.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

/**
 * 공동구매 진행상태가 아닌 상품의 수정을 위한 DTO
 */
public record NonGroupProductUpdateDto(@NotBlank String seller,
                                       @NotBlank String productName,
                                       @Range(min = 0) int basePrice,
                                       @Enumerated(EnumType.STRING) SaleStatus saleStatus,
                                       List<ProductAttributeDto> productAttributeDtos
) {

    /**
     * @param attributeName          상품의 선택항목명 (ex. 색상, 사이즈 등)
     * @param productAttributeValues 상품의 선택항목 값 (ex. 빨강, M 등)
     */
    public record ProductAttributeDto(@NotBlank String attributeName,
                                      List<ProductAttributeValueDto> productAttributeValues
    ) {

        /**
         * @param attributeValue  상품의 선택항목 값 (ex. 빨강, M 등)
         * @param additionalPrice 추가금액
         */
        public record ProductAttributeValueDto(@NotBlank String attributeValue,
                                               @Range(min = 0) int additionalPrice
        ) {}

    }

}
