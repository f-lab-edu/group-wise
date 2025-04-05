package wj.flab.group_wise.dto.gropPurchase;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.Range;

public record GroupPurchaseJoinRequest(
    @NotNull Long memberId,
    @NotNull Long productId,
    List<GroupPurchaseOrderRequest> orders) {

    public record GroupPurchaseOrderRequest(
        @NotNull Long productStockId,
        @Range(min = 1) int quantity
    ) {}
}
