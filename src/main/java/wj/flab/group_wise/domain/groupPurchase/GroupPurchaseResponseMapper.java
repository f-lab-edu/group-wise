package wj.flab.group_wise.domain.groupPurchase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.domain.product.ProductViewResponseMapper;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseStats;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseDetailResponse;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseDetailResponse.GroupPurchaseStockResponse;
import wj.flab.group_wise.dto.product.response.ProductStockResponse;

@Component
@RequiredArgsConstructor
public class GroupPurchaseResponseMapper {

    private final ProductViewResponseMapper productMapper;

    public GroupPurchaseDetailResponse mapToBaseResponse(GroupPurchase groupPurchase, GroupPurchaseStats stats) {
        return new GroupPurchaseDetailResponse(
            groupPurchase.getId(),
            groupPurchase.getTitle(),
            groupPurchase.getProductId(),
            groupPurchase.getDiscountRate(),
            groupPurchase.getMinimumParticipants(),
            groupPurchase.getStartDate(),
            groupPurchase.getEndDate(),
            groupPurchase.getStatus(),
            getRemainingTime(groupPurchase),
            null,
            stats.participantCount(),
            getParticipationRate(groupPurchase),
            stats.wishlistCount(),
            null,
            null
        );
    }

    public Duration getRemainingTime(GroupPurchase groupPurchase) {
        Status status = groupPurchase.getStatus();
        if (status == Status.ONGOING) {
            return Duration.between(LocalDateTime.now(), groupPurchase.getEndDate());
        } else if (status == Status.PENDING) {
            return Duration.between(LocalDateTime.now(), groupPurchase.getStartDate());
        } else {
            return Duration.ZERO;
        }
    }

    public double getParticipationRate(GroupPurchase groupPurchase) {
        long currentParticipantCount = groupPurchase.getGroupPurchaseMembers()
            .stream()
            .filter(GroupPurchaseMember::isHasParticipated)
            .count();

        return (double) currentParticipantCount / groupPurchase.getMinimumParticipants() * 100;
    }

    public List<GroupPurchaseStockResponse> mapToProductStockResponse(GroupPurchase groupPurchase, List<ProductStock> productStocks) {
        return productStocks.stream()
            .map(stock -> {

                // ProductMapper 재사용
                List<ProductStockResponse.ProductAttributeValueResponse> attrValues =
                    productMapper.mapAttributeValuesOfStock(stock.getValues());

                Integer originalPrice = stock.getPrice();
                Integer discountedPrice = calculateDiscountedPrice(originalPrice, groupPurchase.getDiscountRate());
                Long participantCount = groupPurchase.getStockParticipantCount(stock.getId());

                return new GroupPurchaseStockResponse(
                    stock.getId(),
                    stock.getStockQuantity(),
                    attrValues,
                    originalPrice,
                    discountedPrice,
                    participantCount
                );

            })
            .toList();
    }

    private Integer calculateDiscountedPrice(Integer price, Integer discountRate) {
        return price - (price * discountRate / 100);
    }
}