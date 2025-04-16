package wj.flab.group_wise.domain.groupPurchase.event;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public class MinimumParticipantsUnmetEvent extends GroupPurchaseEvent {

    public MinimumParticipantsUnmetEvent(Object source, GroupPurchase groupPurchase) {
        super(source, groupPurchase);
    }
}
