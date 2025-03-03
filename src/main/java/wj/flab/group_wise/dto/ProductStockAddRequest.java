package wj.flab.group_wise.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;

/**
 * 공동구매 진행중 상품의 수정을 위한 DTO
 */
public record ProductStockAddRequest(@NotNull Long productId,
                                     List<StockAddRequest> stockAddRequests
) {

    public record StockAddRequest(@NotNull Long id,
                                  @Range(min = 0) int stockQuantityToBeAdded
    ) {}

}
