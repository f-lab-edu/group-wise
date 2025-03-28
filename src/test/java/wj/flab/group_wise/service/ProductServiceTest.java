package wj.flab.group_wise.service;

import java.util.List;
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
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest.AttributeCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeDeleteRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest.AttributeUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest;
import wj.flab.group_wise.repository.ProductRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDomainDtoCreator productDomainDtoCreator;


    @Test
    void 상품_중복등록시_예외던지기() {

        int attrCount = 2;
        int valuePerAttrCount = 2;

        // given : 상품 추가 정보
        ProductCreateRequest productCreateRequest = productDomainDtoCreator.createProductToCreate(attrCount, valuePerAttrCount);

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
        ProductCreateRequest productCreateRequest = productDomainDtoCreator.createProductToCreate(attrCount, valuePerAttrCount);

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
        ProductCreateRequest productCreateRequest = productDomainDtoCreator.createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 재고 항목 수량을 1로 설정하거나, 재고 항목을 삭제
        Product product = productRepository.findById(productId).orElseThrow(() -> new AssertionError("상품이 생성되지 않았습니다"));
        ProductStockSetRequest productStockSetRequest = productDomainDtoCreator.createStockToSet(productId, product.getProductStocks());
        productService.setProductStock(productId, productStockSetRequest);

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

    @Test
    void 상품_업데이트_재고_수량_수정하기() {
        // given : 상품 추가 후 상품 재고 정보 확인 (2개의 속성, 각 속성당 3개의 값 -> 총 9개의 재고 생성됨)
        ProductCreateRequest productCreateRequest = productDomainDtoCreator.createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 9개의 재고 항목 각각의 수량을 해당 인덱스 값(0부터 8까지)으로 변경
        Product product = productRepository.findById(productId).orElseThrow(() -> new AssertionError("상품이 생성되지 않았습니다"));
        List<ProductStock> stocks = product.getProductStocks();
        int originalStockSize = stocks.size();
        int originalStockQuantity = stocks.stream()
            .mapToInt(ProductStock::getStockQuantity)
            .sum();

        List<StockAddRequest> stockAddRequests = productDomainDtoCreator.createStocksToUpdate(stocks);
        productService.addProductStock(productId, new ProductStockAddRequest(stockAddRequests));
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

    private int calculateTotalQuantityChange(int stockSize) {
        // 0부터 stockSize-1까지의 합
        return stockSize * (stockSize - 1) / 2;
    }

    @Test
    void 상품_업데이트_상세정보_수정하기() {

        // given : 상품 추가 후 상품 재고 정보 확인 (2개의 속성, 각 속성당 2개의 값)
        ProductCreateRequest productCreateRequest = productDomainDtoCreator.createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 상품 상세 정보 수정
        Product product = productRepository.findById(productId).orElseThrow(() -> new AssertionError("상품이 생성되지 않았습니다"));
        List<AttributeCreateRequest> attrsToCreate = productDomainDtoCreator.createAttrsToCreate(2, 2); // 속성 2개 추가
        List<AttributeUpdateRequest> attrsToUpdate = productDomainDtoCreator.createAttrsToUpdate(product.getProductAttributes().get(0));
        List<AttributeDeleteRequest> attrsToDelete = productDomainDtoCreator.createAttrsToDelete(product.getProductAttributes().get(1)); // 속성 1개 삭제

        productService.updateProductDetails(productId,
            new ProductDetailUpdateRequest(
                "seller",
                "productName",
                10000,
                SaleStatus.PREPARE,
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


    @Test
    void 상품_삭제하기() {
        // given : 상품 추가
        ProductCreateRequest productCreateRequest = productDomainDtoCreator.createProductToCreate(2, 3);
        Long productId = productService.createProduct(productCreateRequest);

        // when: 상품 삭제
        productService.deleteProduct(productId);

        // then: 상품이 삭제되었는지 확인
        Assertions.assertThat(productRepository.findById(productId)).isEmpty();
    }

}