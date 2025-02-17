package wj.flab.group_wise.service;

import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.ProductAlreadyExistsException;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.ProductAddDto;
import wj.flab.group_wise.dto.ProductAddDto.ProductAttributeDto;
import wj.flab.group_wise.dto.ProductAddDto.ProductAttributeDto.ProductAttributeValueDto;
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
        return "attributeValue" + AttributeNum + "-" + AttributeValueNum++;
    }
    int getAdditionalPrice() {
        return AdditionalPrice += 1000;
    }

    private List<ProductAttributeValueDto> createAttrValueDtos(int valueCount) {
        return IntStream.range(0, valueCount)
            .mapToObj(j -> new ProductAttributeValueDto(
                this.getAttributeValue(),
                this.getAdditionalPrice()
            ))
            .toList();
    }

    private List<ProductAttributeDto> createAttributeDtos(int attrCount, int valuePerAttrCount) {
        return IntStream.range(0, attrCount)
            .mapToObj(i -> new ProductAttributeDto(
                this.getAttributeName(),
                createAttrValueDtos(valuePerAttrCount)
            ))
            .toList();
    }

    private ProductAddDto createSampleProductAddDto(int attrCount, int valuePerAttrCount) {
        return new ProductAddDto(
            "seller",
            "productName",
            10000,
            SaleStatus.SALE,
            createAttributeDtos(attrCount, valuePerAttrCount)
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
        ProductAddDto productAddDto = createSampleProductAddDto(attrCount, valuePerAttrCount);

        // when : 상품 추가
        productService.addProduct(productAddDto);

        // then : 상품 중복 추가시 예외 반환
        Assertions.assertThatThrownBy(() -> productService.addProduct(productAddDto))
            .isInstanceOf(ProductAlreadyExistsException.class)
            .hasMessage(ProductAlreadyExistsException.MESSAGE);
    }

    @Test
    void 상품_등록_ProductStock_까지_확인하기() {

        int attrCount = 3;
        int valuePerAttrCount = 4;

        // given : 상품 추가 정보
        ProductAddDto productAddDto = createSampleProductAddDto(attrCount, valuePerAttrCount);

        // when : 상품 추가
        productService.addProduct(productAddDto);

        // then : 상품 추가 여부 확인
        List<Product> products = productRepository.findAllWithAttributes();
        long productSize = products.size();
        Assertions.assertThat(productSize).isEqualTo(1);

        int stockSize = products.get(0).getProductStocks().size();
        Assertions.assertThat(stockSize).isEqualTo((int) Math.pow(valuePerAttrCount, attrCount));
    }

}