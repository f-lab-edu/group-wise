package wj.flab.group_wise.domain.groupPurchase.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

@Getter
public abstract class GroupPurchaseEvent extends ApplicationEvent {

    private final GroupPurchase groupPurchase;

    public GroupPurchaseEvent(Object source, GroupPurchase groupPurchase) {
        super(source);
        this.groupPurchase = groupPurchase;
    }
}
