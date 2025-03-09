package wj.flab.group_wise.dto.gropPurchase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;

public record GroupPurchaseCreateRequest(
    @NotBlank String title,
    @NotNull Long productId,
    @Range(min = 0, max = 100) int discountRate,
    @Range(min = 0) int initialPrice,
    @Range(min = 0) int minimumParticipants,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate) {

    public GroupPurchase toEntity() {
        return GroupPurchase.createGroupPurchase(
            title, productId, discountRate, initialPrice, minimumParticipants, startDate, endDate);
    }
}
