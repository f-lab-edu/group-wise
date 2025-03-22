package wj.flab.group_wise.dto.product.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;

/**
 * 상품 재고 수량 설정 및 불필요한 재고 항목 정리을 위한 DTO
 */
public record ProductStockSetRequest(@NotNull Long productId,
                                     List<StockQuantitySetRequest> stockQuantitySetRequests,
                                     List<StockDeleteRequest> stockDeleteRequests
) {

    public record StockQuantitySetRequest(@NotNull Long id,
                                          @Range(min = 0) int stockQuantityToSet
    ) {}

    public record StockDeleteRequest(@NotNull Long id) {}
}
