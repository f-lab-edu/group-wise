package wj.flab.group_wise.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.QProduct;
import wj.flab.group_wise.domain.product.QProductAttribute;
import wj.flab.group_wise.domain.product.QProductAttributeValue;
import wj.flab.group_wise.domain.product.QProductAttributeValueStock;
import wj.flab.group_wise.domain.product.QProductStock;
import wj.flab.group_wise.dto.product.response.ProductAttributeValueResponse;
import wj.flab.group_wise.dto.product.response.ProductAttributeViewResponse;
import wj.flab.group_wise.dto.product.response.ProductStockResponse;
import wj.flab.group_wise.dto.product.response.ProductViewResponse;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ProductViewResponse findProductViewById(Long productId) {
        QProduct product = QProduct.product;
        QProductAttribute attribute = QProductAttribute.productAttribute;
        QProductAttributeValue attributeValue = QProductAttributeValue.productAttributeValue;

        // 상품 기본 정보 조회
        Product foundProduct = queryFactory
            .selectFrom(product)
            .where(product.id.eq(productId))
            .fetchOne();

        if (foundProduct == null) {
            return null;
        }

        // 속성 정보 조회
        List<ProductAttributeViewResponse> attributes = queryFactory
            .select(Projections.constructor(ProductAttributeViewResponse.class,
                product.id,
                attribute.id,
                attribute.attributeName,
                Projections.list(
                    Projections.constructor(ProductAttributeValueResponse.class,
                        product.id,
                        attribute.id,
                        attributeValue.id,
                        attributeValue.attributeValueName,
                        attributeValue.additionalPrice,
                        attributeValue.createdDate,
                        attributeValue.modifiedDate
                    )
                ),
                attribute.createdDate,
                attribute.modifiedDate
            ))
            .from(attribute)
            .join(attribute.product, product)
            .leftJoin(attribute.values, attributeValue)
            .where(product.id.eq(productId))
            .fetch();

        // 상품 재고 정보 조회
        List<ProductStockResponse> stocks = findProductStocksWithAttributeValues(productId);

        return new ProductViewResponse(
            foundProduct.getId(),
            foundProduct.getSeller(),
            foundProduct.getProductName(),
            foundProduct.getBasePrice(),
            foundProduct.getSaleStatus(),
            attributes,
            stocks,
            foundProduct.getCreatedDate(),
            foundProduct.getModifiedDate()
        );
    }

    private List<ProductStockResponse> findProductStocksWithAttributeValues(Long productId) {
        QProduct product = QProduct.product;
        QProductStock stock = QProductStock.productStock;
        QProductAttributeValueStock valueStock = QProductAttributeValueStock.productAttributeValueStock;
        QProductAttributeValue attributeValue = QProductAttributeValue.productAttributeValue;
        QProductAttribute attribute = QProductAttribute.productAttribute;

        // 모든 데이터를 한 번에 가져옴
        List<Tuple> results = queryFactory
            .select(
                product.id,
                stock.id,
                stock.stockQuantity,
                product.basePrice,
                attribute.id,
                attribute.attributeName,
                attributeValue.id,
                attributeValue.attributeValueName,
                attributeValue.additionalPrice,
                stock.createdDate,
                stock.modifiedDate
            )
            .from(stock)
            .join(stock.product, product)
            .leftJoin(stock.values, valueStock)
            .leftJoin(valueStock.productAttributeValue, attributeValue)
            .leftJoin(attributeValue.productAttribute, attribute)
            .where(product.id.eq(productId))
            .orderBy(stock.id.asc())
            .fetch();

        // 결과를 stockId 기준으로 그룹화
        Map<Long, StockInfo> stockInfoMap = new HashMap<>();

        for (Tuple t : results) {
            Long stockId = t.get(stock.id);

            // StockInfo 객체가 없으면 새로 생성
            StockInfo stockInfo = stockInfoMap.computeIfAbsent(stockId, k -> {
                int basePrice = t.get(product.basePrice);
                int stockQuantity = t.get(stock.stockQuantity) != null ? t.get(stock.stockQuantity) : 0;
                LocalDateTime createdDate = t.get(stock.createdDate);
                LocalDateTime modifiedDate = t.get(stock.modifiedDate);

                return new StockInfo(
                    t.get(product.id),
                    stockId,
                    stockQuantity,
                    basePrice,
                    new ArrayList<>(),
                    createdDate,
                    modifiedDate
                );
            });

            // 속성값이 있는 경우에만 추가
            if (t.get(attributeValue.id) != null) {
                ProductStockResponse.ProductAttributeValueResponse attrValue =
                    new ProductStockResponse.ProductAttributeValueResponse(
                        t.get(attribute.id),
                        t.get(attribute.attributeName),
                        t.get(attributeValue.id),
                        t.get(attributeValue.attributeValueName),
                        t.get(attributeValue.additionalPrice)
                    );

                // 중복된 속성값은 추가하지 않음
                if (!stockInfo.attributeValues.contains(attrValue)) {
                    stockInfo.attributeValues.add(attrValue);
                }

                // 추가가격 합산
                stockInfo.additionalPriceSum += t.get(attributeValue.additionalPrice);
            }
        }

        // 최종 응답 리스트 생성
        return stockInfoMap.values().stream()
            .map(info -> new ProductStockResponse(
                info.productId,
                info.stockId,
                info.stockQuantity,
                info.basePrice + info.additionalPriceSum,
                info.attributeValues,
                info.createdDate,
                info.modifiedDate
            ))
            .sorted(Comparator.comparing(ProductStockResponse::stockId))
            .collect(Collectors.toList());
    }

    // 임시 데이터 저장용 클래스
    private static class StockInfo {
        private final Long productId;
        private final Long stockId;
        private final int stockQuantity;
        private final int basePrice;
        private int additionalPriceSum = 0;
        private final List<ProductStockResponse.ProductAttributeValueResponse> attributeValues;
        private final LocalDateTime createdDate;
        private final LocalDateTime modifiedDate;

        public StockInfo(Long productId, Long stockId, int stockQuantity, int basePrice,
            List<ProductStockResponse.ProductAttributeValueResponse> attributeValues,
            LocalDateTime createdDate, LocalDateTime modifiedDate) {
            this.productId = productId;
            this.stockId = stockId;
            this.stockQuantity = stockQuantity;
            this.basePrice = basePrice;
            this.attributeValues = attributeValues;
            this.createdDate = createdDate;
            this.modifiedDate = modifiedDate;
        }
    }
}




