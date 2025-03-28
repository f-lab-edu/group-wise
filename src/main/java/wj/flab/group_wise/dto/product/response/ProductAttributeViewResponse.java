package wj.flab.group_wise.dto.product.response;

import java.time.LocalDateTime;
import java.util.List;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.ProductAttribute;

public record ProductAttributeViewResponse(
    Long productId,
    Long productAttributeId,
    String attributeName,
    List<ProductAttributeValueResponse> attributeValues,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) {

    public static List<ProductAttributeViewResponse> from(Product product) {
        return product.getProductAttributesCopy().stream()
            .map(attribute -> from(product.getId(), attribute))
            .toList();
    }

    private static ProductAttributeViewResponse from(Long productId, ProductAttribute attribute) {

        List<ProductAttributeValueResponse> attributeValues =
            attribute.getValues().stream()
                .map(value ->
                    new ProductAttributeValueResponse(
                        productId,
                        attribute.getId(),
                        value.getId(),
                        value.getAttributeValueName(),
                        value.getAdditionalPrice(),
                        value.getCreatedDate(),
                        value.getModifiedDate()
                    ))
                .toList();

        return new ProductAttributeViewResponse(
            productId,
            attribute.getId(),
            attribute.getAttributeName(),
            attributeValues,
            attribute.getCreatedDate(),
            attribute.getModifiedDate()
        );
    }
}
