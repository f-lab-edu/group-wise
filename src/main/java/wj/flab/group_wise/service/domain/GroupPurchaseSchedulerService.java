package wj.flab.group_wise.service.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseFailureEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseSuccessEvent;
import wj.flab.group_wise.repository.GroupPurchaseRepository;
import wj.flab.group_wise.service.event.GroupPurchaseEventPublisher;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class GroupPurchaseSchedulerService {

    private final GroupPurchaseRepository groupPurchaseRepository;
    private final GroupPurchaseEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    public void checkAndUpdateGroupPurchaseStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<GroupPurchase> expiredGroupPurchases = groupPurchaseRepository
            .findByStatusAndEndDateBefore(Status.ONGOING, now);

        for (GroupPurchase groupPurchase : expiredGroupPurchases) {
            Status newStatus = groupPurchase.complete();

            if (newStatus == Status.COMPLETED_SUCCESS) {
                eventPublisher.publishSuccessEvent(new GroupPurchaseSuccessEvent(this, groupPurchase));
            } else if (newStatus == Status.COMPLETED_FAILURE) {
                eventPublisher.publishFailureEvent(new GroupPurchaseFailureEvent(this, groupPurchase));
            }
        }
    }
}
