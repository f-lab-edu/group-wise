package wj.flab.group_wise.domain.groupPurchase.event;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public class GroupPurchaseSuccessEvent extends GroupPurchaseEvent {

    public GroupPurchaseSuccessEvent(Object source, GroupPurchase groupPurchase) {
        super(source, groupPurchase);
    }
}
