package wj.flab.group_wise.domain.product;

import static lombok.AccessLevel.PROTECTED;

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
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest.StockDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest.StockQuantitySetRequest;
import wj.flab.group_wise.util.ListUtils;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter(/*PROTECTED*/)
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


    public static Product createProduct(String seller, String productName, int basePrice) {
        return new Product(seller, productName, basePrice);
    }

    private Product(String seller, String productName, int basePrice) {
        this.seller = seller;
        this.productName = productName;
        this.basePrice = basePrice;
        this.saleStatus = SaleStatus.PREPARE;
    }

    public void updateProductBasicInfo(String seller, String productName, Integer basePrice, SaleStatus saleStatus) {
        if (seller != null && !seller.isBlank()) this.seller = seller;
        if (productName != null && !productName.isBlank()) this.productName = productName;
        if (basePrice != null) {
            if (basePrice < 0) throw new IllegalArgumentException("상품의 기준가는 0보다 작을 수 없습니다.");
            this.basePrice = basePrice;
        }
        if (saleStatus != null) changeSaleStatus(saleStatus);
    }

    public void changeSaleStatus(SaleStatus saleStatus) {

        if (saleStatus == SaleStatus.SALE) {

            this.productStocks.stream().filter(
                    stock -> !stock.hasStockQuantitySet())
                .findFirst()
                .ifPresent(stock -> {
                    throw new IllegalStateException("재고수량이 설정되지 않은 상품은 판매상태를 판매중으로 설정할 수 없습니다.");});
        }

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
        newAttribute.appendValues(attr.attributeValues());
        productAttributes.add(newAttribute);
    }

    public void restructureAttributes(ProductDetailUpdateRequest productToUpdate) {

        List<AttributeCreateRequest> attrsToCreate = productToUpdate.newAttributes();
        List<AttributeUpdateRequest> attrsToUpdate = productToUpdate.updateAttributes();
        List<AttributeDeleteRequest> attrsToRemove = productToUpdate.deleteAttributesIds();

        boolean hasChangeInAttrValue = attrsToUpdate != null && !attrsToUpdate.isEmpty() && updateAttributes(attrsToUpdate);
        boolean hasAttrToRemove = attrsToRemove != null && !attrsToRemove.isEmpty();
        boolean hasAttrToCreate = attrsToCreate != null && !attrsToCreate.isEmpty();

        if (hasAttrToRemove) removeAttributes(attrsToRemove);
        if (hasAttrToCreate) createAttributes(attrsToCreate);

        boolean requiresStockRenewal
            = hasChangeInAttrValue || hasAttrToRemove || hasAttrToCreate ;

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

            boolean differenceFoundInAdditionalPrice = targetAttr.updateValues(updateValues);
            boolean hasValueToAppend = newValues != null && !newValues.isEmpty();
            boolean hasValueToDelete = deleteValues != null && !deleteValues.isEmpty();

            if (hasValueToAppend) targetAttr.appendValues(newValues);
            if (hasValueToDelete) targetAttr.removeValues(deleteValues);

            hasChangeInValue = differenceFoundInAdditionalPrice || hasValueToAppend || hasValueToDelete;
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
        productStocks.forEach(stock -> stock.getValues().clear());
        productStocks.clear();
        createStockCombinations();
        saleStatus = SaleStatus.PREPARE;
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

    private ProductStock getTargetStock(Long stockId) {
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
