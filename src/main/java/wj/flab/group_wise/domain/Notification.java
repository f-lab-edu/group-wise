package wj.flab.group_wise.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    enum NotificationType {
        SUCCESS, FAILURE, MINIMUM_MET, MINIMUM_UNMET, START, CANCEL;
    }

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long memberId;
    private Long groupPurchaseId;

    private String title;
    private String message;
    private NotificationType notificationType;

    private boolean isRead;
    private String deliveredChannels;
}
