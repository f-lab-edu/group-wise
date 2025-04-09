package wj.flab.group_wise.domain.groupPurchase.command;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public interface GroupPurchaseOrderModifyCommand {
    void execute(GroupPurchase groupPurchase, Long memberId);
}

