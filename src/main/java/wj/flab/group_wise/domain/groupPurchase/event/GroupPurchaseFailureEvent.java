package wj.flab.group_wise.domain.groupPurchase.event;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public class GroupPurchaseFailureEvent extends GroupPurchaseEvent {

    public GroupPurchaseFailureEvent(Object source, GroupPurchase groupPurchase) {
        super(source, groupPurchase);
    }
}
