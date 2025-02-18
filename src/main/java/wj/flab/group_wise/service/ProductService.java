package wj.flab.group_wise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.ProductAttribute;
import wj.flab.group_wise.dto.ProductAddDto;
import wj.flab.group_wise.dto.ProductAddDto.ProductAttributeDto;
import wj.flab.group_wise.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    @Transactional
    public Long addProduct(ProductAddDto productAddDto) {
        Product product = createProduct(productAddDto);
        productRepository.save(product);
        return product.getId();
    }

    private Product createProduct(ProductAddDto productAddDto) {
        // 1. 기본 정보로 상품 생성
        Product product = productAddDto.toEntity();
        productValidator.validateAddProduct(product);

        // 2. 상품 속성 추가
        addProductAttribute(productAddDto.productAttributeDtos(), product);

        // 3. 상품 속성값 조합에 따른 재고 생성
        product.generateProductStocks();
        return product;
    }

    private void addProductAttribute(List<ProductAttributeDto> attributeDtos, Product product) {
        attributeDtos.forEach(attributeDto -> {
            ProductAttribute attribute = new ProductAttribute(
                attributeDto.attributeName(),
                product // 생성 시점에 관계 설정
            );
            product.addProductAttribute(attribute); // 양방향 관계 설정

            attributeDto.productAttributeValues()
                .forEach(valueDto -> attribute.addValue(
                    valueDto.attributeValue(),
                    valueDto.additionalPrice()
                ));
        });
    }

    public Product getProductInfo() {
        // todo DTO 설계가 필요하다
        return null;
    }

    public void updateProduct() {
        // todo 현재 공동구매가 진행 중인 상품인 경우, 특정 필드 수정이 불가능하도록 예외 처리 필요
        // 변경 가능한 entity
        // Product, ProductAttribute, ProductAttributeValue, ProductStock

    }

    public void removeProduct(Long productId) {
        // todo 현재 공동구매가 진행 중인 상품인 경우 예외 처리 필요
        productRepository.deleteById(productId);
    }
}
