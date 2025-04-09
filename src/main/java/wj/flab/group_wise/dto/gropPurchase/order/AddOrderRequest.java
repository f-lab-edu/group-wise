package wj.flab.group_wise.dto.gropPurchase.order;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;

public record AddOrderRequest(Long productStockId, Integer quantity) implements GroupPurchaseOrderModifyCommand {

    @Override
    public void execute(GroupPurchase groupPurchase, Long memberId) {
        groupPurchase.addOrder(memberId, productStockId, quantity);
    }
}
