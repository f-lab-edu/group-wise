package wj.flab.group_wise.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;

/**
 * 상품 재고 수정을 위한 DTO
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
