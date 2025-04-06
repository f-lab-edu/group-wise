package wj.flab.group_wise.dto.gropPurchase;

import java.time.LocalDateTime;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public record GroupPurchaseResponse (
    Long id,
    String title,
    Long productId,
    int discountRate,
    int minimumParticipants,
    int currentParticipants,
    GroupPurchase.Status status,
    LocalDateTime startDate,
    LocalDateTime endDate
){}
