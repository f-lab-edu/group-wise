package wj.flab.group_wise.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseFailureEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseStartedEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseSuccessEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsMetEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsUnmetEvent;

@Component
@RequiredArgsConstructor
public class GroupPurchaseEventListener {
    private final NotificationService notificationService;

    @EventListener
    public void handleGroupPurchaseStartedEvent(GroupPurchaseStartedEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();

        notificationService.notifyMembers(
            groupPurchase.getWishlistIds(),
            "관심 공동구매 시작",
            "공동구매 '" + groupPurchase.getTitle() + "' 진행이 시작되었습니다."
        );
    }

    @EventListener
    public void handleMinimumParticipantsMetEvent(MinimumParticipantsMetEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();
        String message = "'" + groupPurchase.getTitle() + "' 공동구매가 최소 인원을 달성했습니다!";

        notificationService.notifyMembers( groupPurchase.getParticipantIds(), "공동구매 최소 인원 달성", message);
        notificationService.notifyMembers( groupPurchase.getWishlistIds(), "관심 공동구매 최소 인원 달성", message);
    }

    // 최소 인원 미달 상태 알림 처리
    @EventListener
    public void handleMinimumParticipantsUnmet(MinimumParticipantsUnmetEvent event) {
        // todo : 공동구매별로 이전에 최소 인원수 달성했었는지 여부를 확인할 수 있는 필드 추가가 필요함
    }

    @EventListener
    public void handleGroupPurchaseSuccessEvent(GroupPurchaseSuccessEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();
        notificationService.notifyMembers(
            groupPurchase.getParticipantIds(),
            "공동구매 성공",
            "참여하신 '" + groupPurchase.getTitle() + "' 공동구매가 성공적으로 완료되었습니다."
        );

        notificationService.notifyMembers(
            groupPurchase.getWishlistIds(),
            "공동구매 종료",
            "관심 목록에 있는 '" + groupPurchase.getTitle() + "' 공동구매가 성공적으로 종료되었습니다."
        );

        // 주문 생성 로직 추가
    }

    @EventListener
    public void handleGroupPurchaseFailureEvent(GroupPurchaseFailureEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();
        notificationService.notifyMembers(
            groupPurchase.getParticipantIds(),
            "공동구매 실패",
            "참여하신 '" + groupPurchase.getTitle() + "' 공동구매가 최소 인원을 충족하지 못해 취소되었습니다."
        );

        notificationService.notifyMembers(
            groupPurchase.getWishlistIds(),
            "공동구매 종료",
            "관심 목록에 있는 '" + groupPurchase.getTitle() + "' 공동구매가 최소 인원을 충족하지 못해 취소되었습니다."
        );
    }
}
