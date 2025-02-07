package wj.flab.group_wise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.dto.ProductAddDto;
import wj.flab.group_wise.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    public void addProduct(ProductAddDto productAddDto) {
        Product product = productAddDto.toEntity();
        productValidator.validateAddProduct(product);
        productRepository.save(product);
    }

    public Product getProductInfo() {
        // todo DTO 설계가 필요하다
        return null;
    }

    public void updateProduct() {
        // todo 현재 공동구매가 진행 중인 상품인 경우, 특정 필드 수정이 불가능하도록 예외 처리 필요

    }

    public void removeProduct(Long productId) {
        // todo 현재 공동구매가 진행 중인 상품인 경우 예외 처리 필요
        productRepository.deleteById(productId);
    }
}
