package wj.flab.group_wise.dto.groupPurchase.response;

import java.time.Duration;
import java.time.LocalDateTime;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public record GroupPurchaseSummaryResponse (
    long id,
    String title,
    long productId,
    String productName,
    GroupPurchase.Status status,
    int discountRate,
    int cheapestPrice,
    int productStockCount,
    int minimumParticipants,
    long currentParticipantCount,
    double participationRate,
    long wishlistCount,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Duration remainingTime
){}
