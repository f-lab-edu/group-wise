package wj.flab.group_wise.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    public enum NotificationType {
        SUCCESS, FAILURE, MINIMUM_MET, MINIMUM_UNMET, START, CANCEL;
    }

    public enum DeliveryChannel {
        EMAIL, SMS, PUSH;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long memberId;
    private Long groupPurchaseId;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private boolean isRead;

    @Convert(converter = DeliveryChannelSetConverter.class)
    private Set<DeliveryChannel> deliveredChannels;

    private Notification(
        Long groupPurchaseId, NotificationType notificationType,
        String title, String message,
        Long memberId, Set<DeliveryChannel> deliveredChannels) {

        this.groupPurchaseId = groupPurchaseId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.memberId = memberId;
        this.isRead = false;
        this.deliveredChannels = deliveredChannels;
    }

    public static Notification createStartNotification(long groupPurchaseId, String groupPurchaseTitle, long wishMemberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.START,
            "관심 공동구매 시작",
            "공동구매 '" + groupPurchaseTitle + "' 진행이 시작되었습니다.",
            wishMemberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 최소 인원 달성 알림 (참여자용)
    public static Notification createMinimumMetNotificationForParticipant(Long groupPurchaseId, String groupPurchaseTitle, Long memberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.MINIMUM_MET,
            "공동구매 최소 인원 달성",
            "'" + groupPurchaseTitle + "' 공동구매가 최소 인원을 달성했습니다!",
            memberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 최소 인원 달성 알림 (관심 회원용)
    public static Notification createMinimumMetNotificationForWishlist(Long groupPurchaseId, String groupPurchaseTitle, Long memberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.MINIMUM_MET,
            "관심 공동구매 최소 인원 달성",
            "'" + groupPurchaseTitle + "' 공동구매가 최소 인원을 달성했습니다!",
            memberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 공동구매 성공 알림 (참여자용)
    public static Notification createSuccessNotificationForParticipant(Long groupPurchaseId, String groupPurchaseTitle, Long memberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.SUCCESS,
            "공동구매 성공",
            "참여하신 '" + groupPurchaseTitle + "' 공동구매가 성공적으로 완료되었습니다.",
            memberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 공동구매 성공 알림 (관심 회원용)
    public static Notification createSuccessNotificationForWishlist(Long groupPurchaseId, String groupPurchaseTitle, Long memberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.SUCCESS,
            "관심 공동구매 성공",
            "관심 목록에 있는 '" + groupPurchaseTitle + "' 공동구매가 성공적으로 종료되었습니다.",
            memberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 공동구매 실패 알림 (참여자용)
    public static Notification createFailureNotificationForParticipant(Long groupPurchaseId, String groupPurchaseTitle, Long memberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.FAILURE,
            "공동구매 실패",
            "참여하신 '" + groupPurchaseTitle + "' 공동구매가 최소 인원을 충족하지 못해 취소되었습니다.",
            memberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 공동구매 실패 알림 (관심 회원용)
    public static Notification createFailureNotificationForWishlist(Long groupPurchaseId, String groupPurchaseTitle, Long memberId) {
        return new Notification(
            groupPurchaseId,
            NotificationType.FAILURE,
            "관심 공동구매 종료",
            "관심 목록에 있는 '" + groupPurchaseTitle + "' 공동구매가 최소 인원을 충족하지 못해 취소되었습니다.",
            memberId,
            Set.of(DeliveryChannel.EMAIL)
        );
    }

    // 유연한 커스텀 알림 생성 (특별한 경우용)
    public static Notification createCustomNotification(
        Long groupPurchaseId,
        NotificationType type,
        String title,
        String message,
        Long memberId,
        Set<DeliveryChannel> channels) {
        return new Notification(groupPurchaseId, type, title, message, memberId, channels);
    }
}
