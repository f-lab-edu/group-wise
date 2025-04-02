package wj.flab.group_wise.domain.product;

import java.util.List;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.dto.product.response.ProductAttributeValueViewResponse;
import wj.flab.group_wise.dto.product.response.ProductAttributeViewResponse;
import wj.flab.group_wise.dto.product.response.ProductStockResponse;
import wj.flab.group_wise.dto.product.response.ProductViewResponse;

@Component
public class ProductViewResponseMapper {

    public ProductViewResponse mapAttributeValues(Product product) {
        return new ProductViewResponse(
            product.getId(),
            product.getSeller(),
            product.getProductName(),
            product.getBasePrice(),
            product.getSaleStatus(),
            this.mapAttributes(product.getProductAttributes()),
            this.mapStocks(product.getProductStocks()),
            product.getCreatedDate(),
            product.getModifiedDate()
        );
    }

    private List<ProductAttributeViewResponse> mapAttributes(List<ProductAttribute> productAttributes) {
        return productAttributes.stream()
            .map(attr -> new ProductAttributeViewResponse(
                attr.getId(),
                attr.getAttributeName(),
                this.mapAttributeValues(attr.getValues()),
                attr.getCreatedDate(),
                attr.getModifiedDate()
            ))
            .toList();
    }

    private List<ProductAttributeValueViewResponse> mapAttributeValues(List<ProductAttributeValue> values) {
        return values.stream()
            .map(value -> new ProductAttributeValueViewResponse(
                value.getId(),
                value.getAttributeValueName(),
                value.getAdditionalPrice(),
                value.getCreatedDate(),
                value.getModifiedDate()
            ))
            .toList();
    }

    private List<ProductStockResponse> mapStocks(List<ProductStock> productStocks) {
        return productStocks.stream()
            .map(stock -> new ProductStockResponse(
                stock.getId(),
                stock.getStockQuantity(),
                stock.getPrice(),
                this.mapAttributeValuesOfStock(stock.getValues()),
                stock.getCreatedDate(),
                stock.getModifiedDate()
            ))
            .toList();
    }

    private List<ProductStockResponse.ProductAttributeValueResponse> mapAttributeValuesOfStock (List<ProductAttributeValueStock> stockAndValueMaps) {
        return stockAndValueMaps.stream()
            .map(stockValue -> {
                ProductAttributeValue attrValue = stockValue.getProductAttributeValue();
                ProductAttribute attr = attrValue.getProductAttribute();
                return new ProductStockResponse.ProductAttributeValueResponse(
                    attr.getId(),
                    attr.getAttributeName(),
                    attrValue.getId(),
                    attrValue.getAttributeValueName(),
                    attrValue.getAdditionalPrice()
                );
            })
            .toList();
    }
}

