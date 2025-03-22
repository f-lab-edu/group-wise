package wj.flab.group_wise.repository;

import wj.flab.group_wise.dto.product.response.ProductViewResponse;

public interface ProductRepositoryCustom {

    ProductViewResponse findProductViewById(Long productId);

}
