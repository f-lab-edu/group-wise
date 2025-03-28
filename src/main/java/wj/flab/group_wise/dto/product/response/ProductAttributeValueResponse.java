package wj.flab.group_wise.dto.product.response;

import java.time.LocalDateTime;

public record ProductAttributeValueResponse(
    Long productId,
    Long productAttributeId,
    Long productAttributeValueId,
    String attributeValueName,
    int additionalPrice,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) {


}
