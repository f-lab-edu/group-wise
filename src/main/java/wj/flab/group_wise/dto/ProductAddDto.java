package wj.flab.group_wise.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

/**
 * @param seller               판매사
 * @param productName          상품명
 * @param basePrice            기준가(정가)
 * @param saleStatus           판매상태
 * @param productAttributeDtos 상품의 선택항목명과 값
 */
public record ProductAddDto(@NotBlank String seller,
                            @NotBlank String productName,
                            @Range(min = 0) int basePrice,
                            @Enumerated(EnumType.STRING) SaleStatus saleStatus,
                            List<ProductAttributeDto> productAttributeDtos) {

    /**
     * @param attributeName          상품의 선택항목명 (ex. 색상, 사이즈 등)
     * @param productAttributeValues 상품의 선택항목 값 (ex. 빨강, M 등)
     */

    public record ProductAttributeDto(@NotBlank String attributeName, List<ProductAttributeValueDto> productAttributeValues) {

        /**
         * @param attributeValue  상품의 선택항목 값 (ex. 빨강, M 등)
         * @param additionalPrice 추가금액
         */

        public record ProductAttributeValueDto(@NotBlank String attributeValue, @Range(min = 0) int additionalPrice) {}
    }

    public Product toEntity() {
        return Product.createProduct(seller, productName, basePrice, saleStatus);
    }
}
