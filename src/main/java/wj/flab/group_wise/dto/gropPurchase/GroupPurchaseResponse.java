package wj.flab.group_wise.dto.gropPurchase;

public record GroupPurchaseResponse (
    Long id,
    String title,
    Long productId,
    int discountRate,
    int initialPrice,
    int minimumParticipants,
    int currentParticipants,
    String startDate,
    String endDate
){}
