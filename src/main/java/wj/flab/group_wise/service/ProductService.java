package wj.flab.group_wise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest.StockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest;
import wj.flab.group_wise.dto.product.response.ProductViewResponse;
import wj.flab.group_wise.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    public ProductViewResponse getProductInfo(Long productId) {
        ProductViewResponse productInfo = productRepository.findProductViewById(productId);
        if (productInfo == null) {
            throw new EntityNotFoundException(TargetEntity.PRODUCT, productId);
        }
        return productInfo;
    }

    public Long createProduct(ProductCreateRequest productToCreate) {
        Product product = processCreateProduct(productToCreate);
        productRepository.save(product);
        return product.getId();
    }

    private Product processCreateProduct(ProductCreateRequest productToCreate) {
        Product product = productToCreate.toEntity();
        productValidator.validateAddProduct(product);
        product.appendProductAttributes(productToCreate.attributes());
        return product;
    }

    public void setProductStock(Long productId, ProductStockSetRequest productToSetStock) {
        Product product = findProduct(productId);
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);
        product.setProductStocks(productToSetStock.stockQuantitySetRequests());
        product.deleteProductStocks(productToSetStock.stockDeleteRequests());
    }

    public void addProductStock(Long productId, ProductStockAddRequest productToAddStock) {
        Product product = findProduct(productId);
        List<StockAddRequest> stockAddRequests = productToAddStock.stockAddRequests();
        product.addProductStocks(stockAddRequests);
    }

    public void updateProductDetails(Long productId, ProductDetailUpdateRequest productToUpdate) {
        Product product = findProduct(productId);
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);

        product.updateProductBasicInfo(
            productToUpdate.seller(),
            productToUpdate.productName(),
            productToUpdate.basePrice(),
            productToUpdate.saleStatus());

        product.restructureAttributes(productToUpdate);
        log.info(product.toString());
    }

    public void updateProductSaleStatus(Long productId, Product.SaleStatus saleStatus) {
        Product product = findProduct(productId);
        productValidator.validateProductLifeCycleBeforeChangeSaleStatus(product);
        product.changeSaleStatus(saleStatus);
    }

    @Transactional(readOnly = true)
    public Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.PRODUCT, productId));
    }

    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        productValidator.validateProductLifeCycleBeforeMajorUpdate(product);
        productRepository.delete(product);
    }

}
