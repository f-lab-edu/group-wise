package wj.flab.group_wise.domain.groupPurchase.event;

import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public class MinimumParticipantsMetEvent extends GroupPurchaseEvent {

    public MinimumParticipantsMetEvent(Object source, GroupPurchase groupPurchase) {
        super(source, groupPurchase);
    }
}
