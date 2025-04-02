package wj.flab.group_wise.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wj.flab.group_wise.dto.CreateResponse;
import wj.flab.group_wise.dto.product.request.ProductCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductDetailUpdateRequest;
import wj.flab.group_wise.dto.product.request.ProductStockAddRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest;
import wj.flab.group_wise.dto.product.response.ProductViewResponse;
import wj.flab.group_wise.service.ProductService;
import wj.flab.group_wise.service.ProductValidator;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductValidator productValidator;

    @GetMapping("{productId}")
    public ResponseEntity<ProductViewResponse> getProductInfo(@PathVariable("productId") long productId) {
        return ResponseEntity.ok(productService.getProductInfo(productId));
    }

    @PostMapping
    public ResponseEntity<CreateResponse> createProduct(@RequestBody ProductCreateRequest productToCreate) {
        Long productId = productService.createProduct(productToCreate);
        return ResponseEntity.ok(new CreateResponse(productId));
    }

    @PostMapping("{productId}/stocks")
    public ResponseEntity<Void> setProductStock(
        @PathVariable("productId") long productId,
        @RequestBody ProductStockSetRequest productToSetStock) {

        productService.setProductStock(productId, productToSetStock);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{productId}/stocks")
    public ResponseEntity<Void> addProductStock(
        @PathVariable("productId") long productId,
        @RequestBody ProductStockAddRequest productToAddStock) {

        productService.addProductStock(productId, productToAddStock);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{productId}")
    public ResponseEntity<Void> updateProductAndAttribute(
        @PathVariable("productId") long productId,
        @RequestBody ProductDetailUpdateRequest productToUpdate) {

        productService.updateProductDetails(productId, productToUpdate);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }


}
