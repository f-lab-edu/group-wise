package wj.flab.group_wise.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.AlreadyExistsException;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.domain.product.ProductAttribute;
import wj.flab.group_wise.domain.product.ProductAttributeValue;
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.dto.product.ProductCreateRequest;
import wj.flab.group_wise.dto.product.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.product.ProductCreateRequest.AttributeCreateRequest.AttributeValueCreateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeDeleteRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueDeleteRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest.AttributeUpdateRequest.AttributeValueUpdateRequest;
import wj.flab.group_wise.dto.product.ProductStockAddRequest;
import wj.flab.group_wise.dto.product.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.product.ProductStockSetRequest;
import wj.flab.group_wise.dto.product.ProductStockSetRequest.StockDeleteRequest;
import wj.flab.group_wise.dto.product.ProductStockSetRequest.StockQuantitySetRequest;
import wj.flab.group_wise.repository.ProductRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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

    private List<AttributeValueCreateRequest> createAttrValuesToCreate(int valueCount) {
        return IntStream.range(0, valueCount)
            .mapToObj(j -> new AttributeValueCreateRequest(
                this.getAttributeValue(),
                this.getAdditionalPrice()
            ))
            .toList();
    }

    private List<AttributeCreateRequest> createAttrsToCreate(int attrCount, int valuePerAttrCount) {
        return IntStream.range(0, attrCount)
            .mapToObj(i -> new AttributeCreateRequest(
                this.getAttributeName(),
                createAttrValuesToCreate(valuePerAttrCount)
            ))
            .toList();
    }

    private ProductCreateRequest createProductToCreate(int attrCount, int valuePerAttrCount) {
        return new ProductCreateRequest(
            "seller",
            "productName",
            10000,
            SaleStatus.SALE,
            createAttrsToCreate(attrCount, valuePerAttrCount)
        );
    }

//    @BeforeEach
//    void setUp() {
//        productRepository.deleteAll();
//    }

    @Test
    void 상품_중복등록시_예외던지기() {

        int attrCount = 2;
        int valuePerAttrCount = 2;

        // given : 상품 추가 정보
        ProductCreateRequest productCreateRequest = createProductToCreate(attrCount, valuePerAttrCount);

        // when : 상품 추가
        productService.createProduct(productCreateRequest);

        // then : 상품 중복 추가시 예외 반환
        Assertions.assertThatThrownBy(() -> productService.createProduct(productCreateRequest))
            .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void 상품_등록_ProductStock_까지_확인하기() {

        int attrCount = 3;
        int valuePerAttrCount = 4;

        // given : 상품 추가 정보
        ProductCreateRequest productCreateRequest = createProductToCreate(attrCount, valuePerAttrCount);

        // when : 상품 추가
        productService.createProduct(productCreateRequest);

        // then : 상품 추가 여부 확인
        List<Product> products = productRepository.findAllWithAttributes();
        long productSize = products.size();
        Assertions.assertThat(productSize).isEqualTo(1);

        int stockSize = products.get(0).getProductStocks().size();
        Assertions.assertThat(stockSize).isEqualTo((int) Math.pow(valuePerAttrCount, attrCount));
    }

    @Test
    void 상품_재고_수량_설정하기() {
        // given : 상품 추가 후 상품 재고 정보 확인 (2개의 속성, 각 속성당 3개의 값 -> 총 9개의 재고 생성됨)
        ProductCreateRequest productCreateRequest = createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 재고 항목 수량을 1로 설정하거나, 재고 항목을 삭제
        Product product = productRepository.findById(productId).orElseThrow(() -> new AssertionError("상품이 생성되지 않았습니다"));
        ProductStockSetRequest productStockSetRequest = createStockToSet(productId, product.getProductStocks());
        productService.setProductStock(productStockSetRequest);

        // then: 재고 개수와 재고량이 예상대로 변경되었는지 확인
        Product updatedProduct = productRepository.findById(productId).get();
        List<ProductStock> updatedStocks = updatedProduct.getProductStocks();
        int updatedStockSize = updatedStocks.size();
        int updatedStockQuantity = updatedStocks.stream()
            .mapToInt(ProductStock::getStockQuantity)
            .sum();

        Assertions.assertThat(updatedStockSize).isEqualTo(3); // 9개 중 3개만 남음 (i % 4 == 0)
        Assertions.assertThat(updatedStockQuantity).isEqualTo(3 * 5); // 9개 중 3개만 남음 (각 재고 수량 5로 설정)
    }

    private ProductStockSetRequest createStockToSet(Long productId, List<ProductStock> productStocks) {
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

    @Test
    void 상품_업데이트_재고_수량_수정하기() {
        // given : 상품 추가 후 상품 재고 정보 확인 (2개의 속성, 각 속성당 3개의 값 -> 총 9개의 재고 생성됨)
        ProductCreateRequest productCreateRequest = createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 9개의 재고 항목 각각의 수량을 해당 인덱스 값(0부터 8까지)으로 변경
        Product product = productRepository.findById(productId).orElseThrow(() -> new AssertionError("상품이 생성되지 않았습니다"));
        List<ProductStock> stocks = product.getProductStocks();
        int originalStockSize = stocks.size();
        int originalStockQuantity = stocks.stream()
            .mapToInt(ProductStock::getStockQuantity)
            .sum();

        List<StockAddRequest> stockAddRequests = createStocksToUpdate(stocks);
        productService.addProductStock( new ProductStockAddRequest(productId, stockAddRequests) );
        int expectedQuantityChange = calculateTotalQuantityChange(originalStockSize);

        // then: 재고 개수(stock size)는 유지되고, 총 재고량(sum of stock quantity)이 예상대로 변경되었는지 확인
        Product updatedProduct = productRepository.findById(productId).get();

        int updatedStockSize = updatedProduct.getProductStocks().size();
        int updatedStockQuantity = updatedProduct.getProductStocks().stream()
            .mapToInt(ProductStock::getStockQuantity)
            .sum();

        Assertions.assertThat(updatedStockSize).isEqualTo(originalStockSize);
        Assertions.assertThat(updatedStockQuantity).isEqualTo(originalStockQuantity + expectedQuantityChange);
    }

    private List<StockAddRequest> createStocksToUpdate(List<ProductStock> stocks) {
        List<StockAddRequest> stockAddRequests = new ArrayList<>();
        for (int i = 0; i < stocks.size(); i++) {
            stockAddRequests.add(new StockAddRequest(stocks.get(i).getId(), i));
        }
        return stockAddRequests;
    }

    private int calculateTotalQuantityChange(int stockSize) {
        // 0부터 stockSize-1까지의 합
        return stockSize * (stockSize - 1) / 2;
    }

    @Test
    void 상품_업데이트_상세정보_수정하기() {

        // given : 상품 추가 후 상품 재고 정보 확인 (2개의 속성, 각 속성당 2개의 값)
        ProductCreateRequest productCreateRequest = createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 상품 상세 정보 수정
        Product product = productRepository.findById(productId).orElseThrow(() -> new AssertionError("상품이 생성되지 않았습니다"));
        List<AttributeCreateRequest> attrsToCreate = createAttrsToCreate(2, 2); // 속성 2개 추가
        List<AttributeUpdateRequest> attrsToUpdate = createAttrsToUpdate(product.getProductAttributes().get(0));
        List<AttributeDeleteRequest> attrsToDelete = createAttrsToDelete(product.getProductAttributes().get(1)); // 속성 1개 삭제

        productService.updateProductDetails(new ProductDetailUpdateRequest(
            productId,
            "seller",
            "productName",
            10000,
            SaleStatus.SALE,
            attrsToCreate,
            attrsToUpdate,
            attrsToDelete
        ));

        // then: 상품 상세 정보가 예상대로 수정되었는지 확인
        Product updatedProduct = productRepository.findById(productId).get();
        List<ProductAttribute> updatedAttrs = updatedProduct.getProductAttributes();

        // ProductAttribute 개수 확인하기
        Assertions.assertThat(updatedAttrs.size()).isEqualTo(2 + 2 - 1); // 2개 생성 후, 2개 추가, 1개 삭제

        // ProductStock 개수 확인하기
        int expectedStockSize = updatedAttrs.stream()
            .mapToInt(attr -> attr.getValues().size())
            .reduce(1, (a, b) -> a * b); // 모든 속성값의 조합 수
        Assertions.assertThat(updatedProduct.getProductStocks().size()).isEqualTo(expectedStockSize);

    }

    private List<AttributeUpdateRequest> createAttrsToUpdate(ProductAttribute productAttribute) {
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

    private List<AttributeDeleteRequest> createAttrsToDelete(ProductAttribute productAttribute) {
        return List.of(new AttributeDeleteRequest(productAttribute.getId()));
    }

    @Test
    void 상품_삭제하기() {
        // given : 상품 추가
        ProductCreateRequest productCreateRequest = createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 상품 삭제
        productService.deleteProduct(productId);

        // then: 상품이 삭제되었는지 확인
        Assertions.assertThat(productRepository.findById(productId)).isEmpty();
    }

}