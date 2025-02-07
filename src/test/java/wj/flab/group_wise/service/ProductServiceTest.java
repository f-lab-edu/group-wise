package wj.flab.group_wise.service;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.domain.product.ProductAttribute;
import wj.flab.group_wise.dto.ProductAddDto;
import wj.flab.group_wise.dto.ProductAddDto.ProductAttributeDto;
import wj.flab.group_wise.repository.ProductRepository;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void addProduct() {

        // given : 상품 추가 정보
        ProductAddDto productAddDto = new ProductAddDto(
            "seller",
            "productName",
            10000,
            10,
            SaleStatus.SALE,
            List.of(
                new ProductAttributeDto(
                    "색상",
                    List.of(
                        new ProductAttributeDto.ProductAttributeValueDto(
                            "red",
                            1000
                        ),
                        new ProductAttributeDto.ProductAttributeValueDto(
                            "pink",
                            2000
                        )
                    )
                ),
                new ProductAttributeDto(
                    "사이즈",
                    List.of(
                        new ProductAttributeDto.ProductAttributeValueDto(
                            "L",
                            1000
                        ),
                        new ProductAttributeDto.ProductAttributeValueDto(
                            "M",
                            0
                        )
                    )
                )
            )
        );

        // when
        // 상품 추가
        productService.addProduct(productAddDto);

        // then
        // - 중복 상품 추가 시 예외 발생
        Assertions.assertThatThrownBy(() -> productService.addProduct(
                productAddDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 등록된 상품입니다.");

        // - 상품이 정상적으로 추가되었는지 확인
        List<Product> products = productRepository.findAllWithAttributes();
        long count = products.size();
        Assertions.assertThat(count).isEqualTo(1);

        Product product = products.get(0);
        List<ProductAttribute> productAttributes = product.getProductAttributes(); // 지연로딩
        Assertions.assertThat(productAttributes.size()).isEqualTo(2);

//        int ColorValuesCount = productAttributes.stream().filter(pa -> pa.getAttributeName().equals("색상"))
//            .findFirst().get()
//            .getValues().size();
//        Assertions.assertThat(ColorValuesCount).isEqualTo(2);

    }
}