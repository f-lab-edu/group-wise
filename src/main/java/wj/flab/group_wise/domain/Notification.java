package wj.flab.group_wise.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Arrays;
import java.util.stream.Collectors;
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
    private String deliveredChannels;

    public Notification(
        Long groupPurchaseId, NotificationType notificationType,
        String title, String message,
        Long memberId, DeliveryChannel... deliveredChannels) {

        this.groupPurchaseId = groupPurchaseId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.memberId = memberId;
        this.isRead = false;
        this.deliveredChannels = deliveredChannels != null && deliveredChannels.length > 0
            ? Arrays.stream(deliveredChannels)
            .map(DeliveryChannel::name)
            .collect(Collectors.joining(","))
            : DeliveryChannel.EMAIL.name();
    }
}
