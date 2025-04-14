package wj.flab.group_wise.dto.groupPurchase;

import java.time.LocalDateTime;

public record GroupPurchaseUpdateRequest(
    String title,
    Long productId,
    Integer discountRate,
    Integer minimumParticipants,
    LocalDateTime startDate,
    LocalDateTime endDate) {}
