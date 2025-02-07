package wj.flab.group_wise.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.ProductAddDto;
import wj.flab.group_wise.repository.ProductRepository;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Test
    void addProduct() {

        ProductAddDto productAddDto = new ProductAddDto(
            "seller",
            "productName",
            10000,
            10,
            SaleStatus.SALE
        );

        // 상품 추가
        productService.addProduct(productAddDto);

        // 중복 상품 추가 시 예외 발생
        Assertions.assertThatThrownBy(() -> productService.addProduct(
                productAddDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 등록된 상품입니다.");

        long count = productRepository.findAll().size();

        Assertions.assertThat(count).isEqualTo(1);
    }
}