package wj.flab.group_wise.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseFailureEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseStartedEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseSuccessEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsMetEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsUnmetEvent;

@Component
@RequiredArgsConstructor
public class GroupPurchaseEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishStartEvent(GroupPurchaseStartedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishMinimumParticipantsMetEvent(MinimumParticipantsMetEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishMinimumParticipantsUnmetEvent(MinimumParticipantsUnmetEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishSuccessEvent(GroupPurchaseSuccessEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishFailureEvent(GroupPurchaseFailureEvent event) {
        eventPublisher.publishEvent(event);
    }

}
