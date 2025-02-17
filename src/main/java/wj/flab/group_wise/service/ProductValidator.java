package wj.flab.group_wise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.exception.ProductAlreadyExistsException;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.repository.ProductRepository;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;

    public void validateAddProduct(Product product) {
        productRepository.findProductByProductNameAndSeller(product.getProductName(), product.getSeller())
                .ifPresent(p -> {
                    throw new ProductAlreadyExistsException();
                });

    }

}
