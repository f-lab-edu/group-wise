package wj.flab.group_wise.dto.groupPurchase.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record GroupPurchaseJoinRequest(
    @NotNull Long productStockId,
    @Range(min = 1) int quantity ) {}
