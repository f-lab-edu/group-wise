package wj.flab.group_wise.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.domain.product.ProductAttribute;
import wj.flab.group_wise.domain.product.ProductAttributeValue;
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest.StockDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest.StockQuantitySetRequest;

@Component
public class ProductDomainDtoCreator {

    static int AttributeNum = 0;
    static int AttributeValueNum = 0;
    static int AdditionalPrice = 1000;

    String getAttributeName() {
        return "attributeName" + AttributeNum++;
    }

    String getAttributeValue() {
        return "attributeValueName" + (AttributeNum - 1) + "-" + AttributeValueNum++;
    }

    int getAdditionalPrice() {
        return AdditionalPrice += 1000;
    }

    List<AttributeValueCreateRequest> createAttrValuesToCreate(int valueCount) {
        return IntStream.range(0, valueCount)
            .mapToObj(j -> new AttributeValueCreateRequest(
                this.getAttributeValue(),
                this.getAdditionalPrice()
            ))
            .toList();
    }

    List<AttributeCreateRequest> createAttrsToCreate(int attrCount, int valuePerAttrCount) {
        return IntStream.range(0, attrCount)
            .mapToObj(i -> new AttributeCreateRequest(
                this.getAttributeName(),
                createAttrValuesToCreate(valuePerAttrCount)
            ))
            .toList();
    }

    ProductCreateRequest createProductToCreate(int attrCount, int valuePerAttrCount) {
        return new ProductCreateRequest(
            "seller",
            "productName",
            10000,
            SaleStatus.PREPARE,
            createAttrsToCreate(attrCount, valuePerAttrCount)
        );
    }

    List<AttributeDeleteRequest> createAttrsToDelete(ProductAttribute productAttribute) {
        return List.of(new AttributeDeleteRequest(productAttribute.getId()));
    }

    List<AttributeUpdateRequest> createAttrsToUpdate(ProductAttribute productAttribute) {
        ProductAttributeValue valueToUpdate_1 = productAttribute.getValues().get(0);
        ProductAttributeValue valueToUpdate_2 = productAttribute.getValues().get(1);
        ProductAttributeValue valueToDelete = productAttribute.getValues().get(2);
        return List.of(
            new AttributeUpdateRequest(productAttribute.getId(), productAttribute.getAttributeName() + "_updated",
                createAttrValuesToCreate(2),
                List.of(
                    new AttributeValueUpdateRequest(valueToUpdate_1.getId(),
                        valueToUpdate_1.getAttributeValueName() + "_updated",
                        valueToUpdate_1.getAdditionalPrice() + 1000),
                    new AttributeValueUpdateRequest(valueToUpdate_2.getId(),
                        valueToUpdate_2.getAttributeValueName() + "_updated",
                        valueToUpdate_2.getAdditionalPrice() + 1000)),
                List.of(
                    new AttributeValueDeleteRequest(valueToDelete.getId()))));
    }

    ProductStockSetRequest createStockToSet(Long productId, List<ProductStock> productStocks) {
        List<StockQuantitySetRequest> stockQuantitySetRequests = new ArrayList<>();
        List<StockDeleteRequest> stockDeleteRequests = new ArrayList<>();

        for (int i = 0; i < productStocks.size(); i++) {
            if (i % 4 == 0) {
                stockQuantitySetRequests.add(new StockQuantitySetRequest(productStocks.get(i).getId(), 5));
            } else {
                stockDeleteRequests.add(new StockDeleteRequest(productStocks.get(i).getId()));
            }
        }

        return new ProductStockSetRequest(productId,
            stockQuantitySetRequests,
            stockDeleteRequests
        );
    }

    List<StockAddRequest> createStocksToUpdate(List<ProductStock> stocks) {
        List<StockAddRequest> stockAddRequests = new ArrayList<>();
        for (int i = 0; i < stocks.size(); i++) {
            stockAddRequests.add(new StockAddRequest(stocks.get(i).getId(), i));
        }
        return stockAddRequests;
    }

}
