package wj.flab.group_wise.dto.groupPurchase.response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.dto.product.response.ProductStockResponse;
import wj.flab.group_wise.dto.product.response.ProductViewResponse;
//import wj.flab.group_wise.dto.product.response.ProductViewResponse;

public record GroupPurchaseDetailResponse (
    Long id,
    String title,
    Long productId,
    Integer discountRate,
    Integer minimumParticipants,
    LocalDateTime startDate,
    LocalDateTime endDate,
    GroupPurchase.Status status,
    Duration remainingTime,             // 남은 시간
    Integer cheapestPrice,              // productStock 최저가 (할인후)
    Long currentParticipantCount,       // 공동구매에 참여한 총 고유 회원 수
    Double participationRate,           // 공동구매 참여율
    Long wishlistCount,                 // 즐겨찾기 인원수
    ProductViewResponse product,
    List<GroupPurchaseStockResponse> productStocks
) {

    public GroupPurchaseDetailResponse withInfo (
        ProductViewResponse productResponse,
        Integer cheapestPrice,
        List<GroupPurchaseStockResponse> productStocks ) {
        return new GroupPurchaseDetailResponse(
            id,
            title,
            productId,
            discountRate,
            minimumParticipants,
            startDate,
            endDate,
            status,
            remainingTime,
            cheapestPrice,
            currentParticipantCount,
            participationRate,
            wishlistCount,
            productResponse,
            productStocks
        );
    }

    public record GroupPurchaseStockResponse(
        Long stockId,
        Integer stockQuantity,
        List<ProductStockResponse.ProductAttributeValueResponse> attributeValues,
        Integer originalPrice,
        Integer discountedPrice,
        Long stockParticipantCount
    ) {}

}
