package wj.flab.group_wise.dto.product.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductStockResponse(
    Long stockId,
    int stockQuantity,
    int price,
    List<ProductAttributeValueResponse> attributeValues,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) {

    public record ProductAttributeValueResponse(
        Long productAttributeId,
        String attributeName,
        Long productAttributeValueId,
        String attributeValueName,
        int additionalPrice
    ) {}

}
