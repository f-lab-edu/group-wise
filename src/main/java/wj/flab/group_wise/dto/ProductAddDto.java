package wj.flab.group_wise.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

@RequiredArgsConstructor
public class ProductAddDto {

    @Getter
    @RequiredArgsConstructor
    public static class ProductAttributeDto {

        @NotBlank
        private final String attributeName;                                       // 상품의 선택항목명 (ex. 색상, 사이즈 등)
        private final List<ProductAttributeValueDto> productAttributeValues;   // 상품의 선택항목 값 (ex. 빨강, M 등)

        @Getter
        @RequiredArgsConstructor
        public static class ProductAttributeValueDto {
            @NotBlank
            private final String attributeValue;              // 상품의 선택항목 값 (ex. 빨강, M 등)

            @Range(min = 0)
            private final int additionalPrice;                // 추가금액
        }
    }

    @NotBlank
    private final String seller;                      // 판매사

    @NotBlank
    private final String productName;                 // 상품명

    @Range(min = 0)
    private final int basePrice;                      // 기준가(정가)

    @Range(min = 0)
    private final int availableQuantity;              // 공구 가능한 수량

    @Enumerated(EnumType.STRING)
    private final SaleStatus saleStatus;              // 판매상태

    private final List<ProductAttributeDto> productAttributes;    // 상품의 선택항목명과 값

    public Product toEntity() {
        return Product.createProduct(seller, productName, basePrice, availableQuantity, productAttributes);
    }
}
