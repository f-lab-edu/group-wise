package wj.flab.group_wise.dto.gropPurchase.order;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;

public record ModifyOrderQuantityRequest(Long productStockId, Integer quantity) implements GroupPurchaseOrderModifyCommand {

    @Override
    public void execute(GroupPurchase groupPurchase, Long memberId) {
        groupPurchase.updateOrder(memberId, productStockId, quantity);
    }
}
