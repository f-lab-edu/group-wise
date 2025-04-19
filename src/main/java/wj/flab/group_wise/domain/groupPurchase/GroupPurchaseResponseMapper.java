package wj.flab.group_wise.domain.groupPurchase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.domain.product.ProductViewResponseMapper;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseDetailResponse;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseDetailResponse.GroupPurchaseStockResponse;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseSummaryResponse;
import wj.flab.group_wise.dto.product.response.ProductStockResponse;

@Component
@RequiredArgsConstructor
public class GroupPurchaseResponseMapper {

    private final ProductViewResponseMapper productMapper;

    public GroupPurchaseSummaryResponse mapToSummaryResponse(GroupPurchase groupPurchase, Product product, int cheapestPrice) {
        return new GroupPurchaseSummaryResponse(
            groupPurchase.getId(),
            groupPurchase.getTitle(),
            groupPurchase.getProductId(),
            product.getProductName(),
            groupPurchase.getStatus(),
            groupPurchase.getDiscountRate(),
            cheapestPrice,
            product.getProductStocks().size(),
            groupPurchase.getMinimumParticipants(),
            groupPurchase.getCurrentParticipantCount(),
            groupPurchase.getParticipationRate(),
            groupPurchase.getWishlistCount(),
            groupPurchase.getStartDate(),
            groupPurchase.getEndDate(),
            groupPurchase.getRemainingTime()
        );
    }

    public GroupPurchaseDetailResponse mapToBaseResponse(GroupPurchase groupPurchase) {
        return new GroupPurchaseDetailResponse(
            groupPurchase.getId(),
            groupPurchase.getTitle(),
            groupPurchase.getProductId(),
            groupPurchase.getDiscountRate(),
            groupPurchase.getMinimumParticipants(),
            groupPurchase.getStartDate(),
            groupPurchase.getEndDate(),
            groupPurchase.getStatus(),
            groupPurchase.getRemainingTime(),
            null,
            groupPurchase.getCurrentParticipantCount(),
            groupPurchase.getParticipationRate(),
            groupPurchase.getWishlistCount(),
            null,
            null
        );
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