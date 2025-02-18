package wj.flab.group_wise.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

/**
 * 공동구매 진행상태가 아닌 상품의 수정을 위한 DTO
 */
public record GroupingProductUpdateDto(@NotNull Long id,
                                       @NotBlank String seller,
                                       @NotBlank String productName,
                                       @Range(min = 0) int basePrice,
                                       @Enumerated(EnumType.STRING) SaleStatus saleStatus,
                                       List<ProductStockDto> productStockDtoDtos
) {

    public record ProductStockDto(@NotNull Long id,
                                  @Range(min = 0) int stockQuantity
    ) {}
}
