package wj.flab.group_wise.domain.groupPurchase.event;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public class GroupPurchaseStartedEvent extends GroupPurchaseEvent {

    public GroupPurchaseStartedEvent(Object source, GroupPurchase groupPurchase) {
        super(source, groupPurchase);
    }
}
