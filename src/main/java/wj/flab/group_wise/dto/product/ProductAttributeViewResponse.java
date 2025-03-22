package wj.flab.group_wise.dto.product;

import java.time.LocalDateTime;
import java.util.List;

public record ProductAttributeViewResponse(
    Long productId,
    Long productAttributeId,
    String attributeName,
    List<ProductAttributeValueResponse> attributeValues,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) {

}
