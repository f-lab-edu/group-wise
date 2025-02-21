package wj.flab.group_wise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wj.flab.group_wise.domain.exception.ProductAlreadyExistsException;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.repository.GroupPurchaseRepository;
import wj.flab.group_wise.repository.ProductRepository;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;

    public void validateAddProduct(Product product) {
        productRepository.findProductByProductNameAndSeller(product.getProductName(), product.getSeller())
                .ifPresent(p -> {
                    throw new ProductAlreadyExistsException();
                });
    }

    public void validateProductLifeCycleBeforeMajorUpdate(Product product) {
        List<GroupPurchase> groupPurchases = groupPurchaseRepository.findByProduct(product);

        if (groupPurchases.stream().anyMatch(GroupPurchase::isOngoing))
            throw new IllegalStateException("진행 중인 공동구매 상품은 재고 증가만 가능합니다.");
    }

}
