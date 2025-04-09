package wj.flab.group_wise.dto.gropPurchase.order;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;

public record DeleteOrderRequest(Long productStockId) implements GroupPurchaseOrderModifyCommand {

    @Override
    public void execute(GroupPurchase groupPurchase, Long memberId) {
        groupPurchase.deleteOrder(memberId, productStockId);
    }
}
