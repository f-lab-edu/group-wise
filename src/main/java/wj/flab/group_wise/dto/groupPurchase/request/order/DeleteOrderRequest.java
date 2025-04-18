package wj.flab.group_wise.dto.groupPurchase.request.order;

import com.fasterxml.jackson.annotation.JsonTypeName;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;
@JsonTypeName("DeleteOrderRequest")
public record DeleteOrderRequest(Long productStockId) implements GroupPurchaseOrderModifyCommand {

    @Override
    public void execute(GroupPurchase groupPurchase, Long memberId) {
        groupPurchase.deleteOrder(memberId, productStockId);
    }
}
