package wj.flab.group_wise.dto.product.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductAttributeViewResponse(
    Long productAttributeId,
    String attributeName,
    List<ProductAttributeValueViewResponse> attributeValues,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) {}
