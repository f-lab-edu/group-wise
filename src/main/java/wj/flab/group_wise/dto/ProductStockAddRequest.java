package wj.flab.group_wise.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;

/**
 * 상품 재고 추가를 위한 DTO
 */
public record ProductStockAddRequest(@NotNull Long productId,
                                     List<StockAddRequest> stockAddRequests
) {

    public record StockAddRequest(@NotNull Long id,
                                  @Range(min = 0) int stockQuantityToBeAdded
    ) {}

}
