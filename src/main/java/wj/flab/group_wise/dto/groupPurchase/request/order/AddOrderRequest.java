package wj.flab.group_wise.dto.groupPurchase.request.order;

import com.fasterxml.jackson.annotation.JsonTypeName;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;

@JsonTypeName("AddOrderRequest")
public record AddOrderRequest(Long productStockId, Integer quantity) implements GroupPurchaseOrderModifyCommand {

    @Override
    public void execute(GroupPurchase groupPurchase, Long memberId) {
        groupPurchase.addItem(memberId, productStockId, quantity);
    }
}
