package wj.flab.group_wise.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

@RequiredArgsConstructor
public class ProductAddDto {

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

    public Product toEntity() {
        return Product.createProduct(seller, productName, basePrice, availableQuantity);
    }
}
