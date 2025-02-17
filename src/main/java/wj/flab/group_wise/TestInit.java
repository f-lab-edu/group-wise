package wj.flab.group_wise;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.ProductAddDto;
import wj.flab.group_wise.dto.ProductAddDto.ProductAttributeDto;
import wj.flab.group_wise.service.ProductService;

@Component
@RequiredArgsConstructor
public class TestInit {

    private final ProductService productService;

    @PostConstruct
    public void init() {
        // given : 상품 추가 정보
        ProductAddDto productAddDto = new ProductAddDto(
            "seller",
            "productName",
            10000,
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
    }
}
