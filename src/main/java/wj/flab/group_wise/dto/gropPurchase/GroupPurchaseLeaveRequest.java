package wj.flab.group_wise.dto.gropPurchase;

import jakarta.validation.constraints.NotNull;

public record GroupPurchaseLeaveRequest(
    @NotNull Long memberId
    ) {}
