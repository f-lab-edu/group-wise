package wj.flab.group_wise.dto.gropPurchase;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record GroupPurchaseJoinRequest(
    @NotNull Long memberId,
    @NotNull Long productId,
    @NotNull Long productStockId,
    @Range(min = 1) int quantity
    ) {}
