package wj.flab.group_wise.dto.product;

import java.time.LocalDateTime;
import java.util.List;
import wj.flab.group_wise.domain.product.Product.SaleStatus;

public record ProductViewResponse (
    Long productId,
    String seller,
    String productName,
    int basePrice,
    SaleStatus saleStatus,
    List<ProductAttributeViewResponse> productAttributes,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
){

}
