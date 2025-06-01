package wj.flab.group_wise.service.event;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import wj.flab.group_wise.domain.Notification;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseFailureEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseStartedEvent;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseSuccessEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsMetEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsUnmetEvent;
import wj.flab.group_wise.service.domain.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseEventListener {

    private final NotificationService notificationService;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGroupPurchaseStartedEvent(GroupPurchaseStartedEvent event) {

        GroupPurchase groupPurchase = event.getGroupPurchase();

        Map<Boolean, Long> results = groupPurchase.getWishlistIds().parallelStream()
            .map(wishlistId -> notificationService.notify(
                Notification.createStartNotification(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    wishlistId)))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        log.info("공동구매 시작 알림 발송 완료. 성공: {}명, 실패: {}명",
            results.getOrDefault(true, 0L), results.getOrDefault(false, 0L));
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMinimumParticipantsMetEvent(MinimumParticipantsMetEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();

        // 참여자들에게 알림
        Map<Boolean, Long> participantResults = groupPurchase.getParticipantIds().parallelStream()
            .map(participantId -> notificationService.notify(
                Notification.createMinimumMetNotificationForParticipant(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    participantId
                )
            ))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        // 관심 회원들에게 알림
        Map<Boolean, Long> wishlistResults = groupPurchase.getWishlistIds().parallelStream()
            .map(wishlistId -> notificationService.notify(
                Notification.createMinimumMetNotificationForWishlist(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    wishlistId
                )
            ))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        log.info("최소 인원 달성 알림 발송 완료. 참여자 성공: {}명, 실패: {}명 | 관심회원 성공: {}명, 실패: {}명",
            participantResults.getOrDefault(true, 0L), participantResults.getOrDefault(false, 0L),
            wishlistResults.getOrDefault(true, 0L), wishlistResults.getOrDefault(false, 0L));
    }

    // 최소 인원 미달 상태 알림 처리
    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMinimumParticipantsUnmet(MinimumParticipantsUnmetEvent event) {
        // todo : 공동구매별로 이전에 최소 인원수 달성했었는지 여부를 확인할 수 있는 필드 추가가 필요함
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGroupPurchaseSuccessEvent(GroupPurchaseSuccessEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();

        // 참여자들에게 성공 알림
        Map<Boolean, Long> participantResults = groupPurchase.getParticipantIds().parallelStream()
            .map(participantId -> notificationService.notify(
                Notification.createSuccessNotificationForParticipant(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    participantId
                )
            ))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        // 관심 회원들에게 성공 알림
        Map<Boolean, Long> wishlistResults = groupPurchase.getWishlistIds().parallelStream()
            .map(wishlistId -> notificationService.notify(
                Notification.createSuccessNotificationForWishlist(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    wishlistId
                )
            ))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        log.info("공동구매 성공 알림 발송 완료. 참여자 성공: {}명, 실패: {}명 | 관심회원 성공: {}명, 실패: {}명",
            participantResults.getOrDefault(true, 0L), participantResults.getOrDefault(false, 0L),
            wishlistResults.getOrDefault(true, 0L), wishlistResults.getOrDefault(false, 0L));

        // 주문 생성 로직 추가
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGroupPurchaseFailureEvent(GroupPurchaseFailureEvent event) {
        GroupPurchase groupPurchase = event.getGroupPurchase();

        // 참여자들에게 실패 알림
        Map<Boolean, Long> participantResults = groupPurchase.getParticipantIds().parallelStream()
            .map(participantId -> notificationService.notify(
                Notification.createFailureNotificationForParticipant(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    participantId
                )
            ))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        // 관심 회원들에게 실패 알림
        Map<Boolean, Long> wishlistResults = groupPurchase.getWishlistIds().parallelStream()
            .map(wishlistId -> notificationService.notify(
                Notification.createFailureNotificationForWishlist(
                    groupPurchase.getId(),
                    groupPurchase.getTitle(),
                    wishlistId
                )
            ))
            .collect(Collectors.groupingBy(result -> result, Collectors.counting()));

        log.info("공동구매 실패 알림 발송 완료. 참여자 성공: {}명, 실패: {}명 | 관심회원 성공: {}명, 실패: {}명",
            participantResults.getOrDefault(true, 0L), participantResults.getOrDefault(false, 0L),
            wishlistResults.getOrDefault(true, 0L), wishlistResults.getOrDefault(false, 0L));
    }
}
