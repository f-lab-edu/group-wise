package wj.flab.group_wise.domain.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.dto.product.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.product.ProductStockSetRequest.StockDeleteRequest;
import wj.flab.group_wise.dto.product.ProductStockSetRequest.StockQuantitySetRequest;
import wj.flab.group_wise.dto.product.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.product.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeDeleteRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueDeleteRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueUpdateRequest;
import wj.flab.group_wise.util.ListUtils;

@Entity @Getter
public class Product extends BaseTimeEntity {

    public enum SaleStatus {
        PREPARE,    // 준비중
        SALE,       // 판매중
        SOLD_OUT,   // 품절
        DISCONTINUE // 단종
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String seller;                      // 판매사

    @NotBlank
    private String productName;                 // 상품명

    @Range(min = 0)
    private int basePrice;                      // 기준가(정가)

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;              // 판매상태

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<ProductAttribute> productAttributes = new ArrayList<>();  // 상품의 선택항목

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<ProductStock> productStocks = new ArrayList<>();           // 상품의 선택항목 조합에 따른 재고

    public static Product createProduct(String seller, String productName, int basePrice, SaleStatus saleStatus) {
        return new Product(seller, productName, basePrice, saleStatus);
    }

    protected Product() {
    }

    private Product(String seller, String productName, int basePrice, SaleStatus saleStatus) {
        this.seller = seller;
        this.productName = productName;
        this.basePrice = basePrice;
        this.saleStatus = saleStatus;
    }

    public void updateProductBasicInfo(String seller, String productName, int basePrice, SaleStatus saleStatus) {
        this.seller = seller;
        this.productName = productName;
        this.basePrice = basePrice;
        this.saleStatus = saleStatus;
    }

    /*
    요약 : Dto 에 의존하지 않는 entity 를 설계하고 싶었으나, 일단 Dto 를 참조하도록 했습니다.
    상세 :
        Product 를 애그리거트 루트로 설정하고,
        외부에서 애그리거트 루트 내부의 entity 에 직접 접근하지 못하게 하려다보니
        중첩된 Dto Mapping 이 어려워졌습니다. (entity 생성자도 protected 로 설정했기 때문입니다.)
        애그리거트 내 entity 외부접근 제한 유지하면서 Dto 의존성을 피할 수 있는 방법을 claude AI 에게 물어보았는데요.
        AI는 DTO 대신 Command Pattern 을 사용할 것을 권했으나,
        작업 비용이 생각한 것보다 커지는 것 같아서, 일단 entity 에서 DTO 를 참조했습니다. (최대한 dto 의존을 최소화하려고 합니다)
     */
    public void appendProductAttributes(List<AttributeCreateRequest> attrToCreate) {
        attrToCreate.forEach(this::convertToAttrEntityAndAppendToAttrList);
        createStockCombinations();
    }

    private void convertToAttrEntityAndAppendToAttrList(AttributeCreateRequest attr) {
        ProductAttribute newAttribute = new ProductAttribute(attr.attributeName(), this);
        newAttribute.appendValues(attr.productAttributeValues());
        productAttributes.add(newAttribute);
    }

    public void restructureAttributes(ProductDetailUpdateRequest productToUpdate) {

        List<AttributeCreateRequest> attrsToCreate = productToUpdate.newAttributes();
        List<AttributeUpdateRequest> attrsToUpdate = productToUpdate.updateAttributes();
        List<AttributeDeleteRequest> attrsToRemove = productToUpdate.deleteAttributesIds();

        createAttributes(attrsToCreate);
        removeAttributes(attrsToRemove);
        boolean hasChangeInValue = updateAttributes(attrsToUpdate);

        boolean requiresStockRenewal
            = hasChangeInValue || !attrsToCreate.isEmpty() || !attrsToRemove.isEmpty() ;

        if (requiresStockRenewal) {
            renewProductStocks();
        }
    }

    private void createAttributes(List<AttributeCreateRequest> attrsToCreate) {
        attrsToCreate.forEach(this::convertToAttrEntityAndAppendToAttrList);
    }

    private boolean updateAttributes(List<AttributeUpdateRequest> attrsToUpdate) {
        boolean hasChangeInValue = false;

        for (AttributeUpdateRequest attr : attrsToUpdate) {

            ProductAttribute targetAttr = getProductAttribute(attr.productAttributeId());
            targetAttr.updateAttributeName(attr.attributeName());

            List<AttributeValueCreateRequest> newValues = attr.newAttributeValues();
            List<AttributeValueUpdateRequest> updateValues = attr.updateAttributeValues();
            List<AttributeValueDeleteRequest> deleteValues = attr.deleteAttributeValuesIds();

            targetAttr.appendValues(newValues);
            targetAttr.removeValues(deleteValues);
            boolean differenceFoundInAdditionalPrice = targetAttr.updateValues(updateValues);

            hasChangeInValue = differenceFoundInAdditionalPrice || !newValues.isEmpty() || !deleteValues.isEmpty();
        }
        return hasChangeInValue;
    }

    private void removeAttributes(List<AttributeDeleteRequest> attrDeleteIds) {
        attrDeleteIds.forEach(attr -> {
            ProductAttribute targetAttr = getProductAttribute(attr.productAttributeId());
            productAttributes.remove(targetAttr);
        });
    }

    private void renewProductStocks() {
        productStocks.clear();
        createStockCombinations();
    }

    private void createStockCombinations() {
        if (productAttributes.isEmpty()) {
            ProductStock newStock = new ProductStock(this);
            productStocks.add(newStock);
        } else {
            List<List<ProductAttributeValue>> attrValueCombinations = ListUtils.cartesianProduct(productAttributes);
            attrValueCombinations.forEach(combination -> {
                ProductStock newStock = new ProductStock(this, combination);
                productStocks.add(newStock);
            });
        }
    }

    public void addProductStocks(List<StockAddRequest> stockAddRequests) {
        stockAddRequests.forEach(stockDto -> {
            ProductStock targetStock = getTargetStock(stockDto.id());
            targetStock.addStockQuantity(stockDto.stockQuantityToBeAdded());
        });
    }

    public void setProductStocks(List<StockQuantitySetRequest> stockQuantitySetRequests) {
        stockQuantitySetRequests.forEach(stockDto -> {
            ProductStock targetStock = getTargetStock(stockDto.id());
            targetStock.setStockQuantity(stockDto.stockQuantityToSet());
        });
    }

    public void deleteProductStocks(List<StockDeleteRequest> stockDeleteRequests) {
        stockDeleteRequests.forEach(stockDto -> {
            ProductStock targetStock = getTargetStock(stockDto.id());
            productStocks.remove(targetStock);
        });
    }

    private ProductAttribute getProductAttribute(Long productAttributeId) {
        return productAttributes.stream()
            .filter(attr -> attr.getId().equals(productAttributeId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.PRODUCT_ATTRIBUTE, productAttributeId));
    }

    public ProductStock getTargetStock(Long stockId) {
        return productStocks.stream()
            .filter(s -> s.getId().equals(stockId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.PRODUCT_STOCK, stockId));
    }

    public void decreaseStockQuantity(Long stockId, int quantity) {
        ProductStock targetStock = getTargetStock(stockId);
        targetStock.decreaseStockQuantity(quantity);
    }
}
