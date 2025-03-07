package wj.flab.group_wise.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.dto.ProductCreateRequest;
import wj.flab.group_wise.dto.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.ProductStockAddRequest;
import wj.flab.group_wise.dto.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.ProductStockSetRequest;
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

    public Long createProduct(ProductCreateRequest productToCreate) {
        Product product = processCreateProduct(productToCreate);
        productRepository.save(product);
        return product.getId();
    }

    private Product processCreateProduct(ProductCreateRequest productToCreate) {
        Product product = productToCreate.toEntity();
        productValidator.validateAddProduct(product);
        product.appendProductAttributes(productToCreate.attributeAddDtos());
        return product;
    }

    public void setProductStock(ProductStockSetRequest productToSetStock) {
        Product product = getProduct(productToSetStock.productId());
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);
        product.setProductStocks(productToSetStock.stockQuantitySetRequests());
        product.deleteProductStocks(productToSetStock.stockDeleteRequests());
    }

    public void addProductStock(ProductStockAddRequest productToAddStock) {
        Product product = getProduct(productToAddStock.productId());
        List<StockAddRequest> stockAddRequests = productToAddStock.stockAddRequests();
        product.addProductStocks(stockAddRequests);
    }

    public void updateProductDetails(ProductDetailUpdateRequest productToUpdate) {
        Product product = getProduct(productToUpdate.productId());
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);

        product.updateProductBasicInfo(
            productToUpdate.seller(),
            productToUpdate.productName(),
            productToUpdate.basePrice(),
            productToUpdate.saleStatus());

        product.restructureAttributes(productToUpdate);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("%d", productId)));
    }

    public void deleteProduct(Long productId) {
        Product product = getProduct(productId);
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);
        productRepository.delete(product);
    }

}
