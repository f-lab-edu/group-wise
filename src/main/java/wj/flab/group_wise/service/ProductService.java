package wj.flab.group_wise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.dto.product.ProductCreateRequest;
import wj.flab.group_wise.dto.product.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.ProductStockUpdateRequest;
import wj.flab.group_wise.dto.product.ProductStockUpdateRequest.ProductStockDto;
import wj.flab.group_wise.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    public Product getProductInfo() {
        // todo DTO 설계가 필요하다
        return null;
    }

    public Long createProduct(ProductCreateRequest productCreateRequest) {
        Product product = processCreateProduct(productCreateRequest);
        productRepository.save(product);
        return product.getId();
    }

    private Product processCreateProduct(ProductCreateRequest productCreateRequest) {
        Product product = productCreateRequest.toEntity();
        productValidator.validateAddProduct(product);
        product.appendProductAttributes(productCreateRequest.attributeAddDtos());
        return product;
    }

    public void updateProductStock(ProductStockUpdateRequest productToUpdate) {
        Product product = findProduct(productToUpdate.productId());
        List<ProductStockDto> productStockDtos = productToUpdate.productStockDtos();
        product.updateProductStocks(productStockDtos);
    }

    public void updateProductDetails(ProductDetailUpdateRequest productToUpdate) {
        Product product = findProduct(productToUpdate.productId());
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);

        product.updateProductBasicInfo(
            productToUpdate.seller(),
            productToUpdate.productName(),
            productToUpdate.basePrice(),
            productToUpdate.saleStatus());

        product.restructureAttributes(productToUpdate);
    }

    public Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.PRODUCT, productId));
    }

}
