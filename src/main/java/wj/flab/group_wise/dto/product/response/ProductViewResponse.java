package wj.flab.group_wise.dto.product.response;

import java.time.LocalDateTime;
import java.util.List;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

public record ProductViewResponse (
    Long productId,
    String seller,
    String productName,
    int basePrice,
    SaleStatus saleStatus,
    List<ProductAttributeViewResponse> productAttributes,
    List<ProductStockResponse> productStocks,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
){
    public static ProductViewResponse from(Product product) {
        return new ProductViewResponse(
            product.getId(),
            product.getSeller(),
            product.getProductName(),
            product.getBasePrice(),
            product.getSaleStatus(),
            ProductAttributeViewResponse.from(product),
            product.getProductStockResponses(),
            product.getCreatedDate(),
            product.getModifiedDate()
        );
    }
}
