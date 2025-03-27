package wj.flab.group_wise.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import wj.flab.group_wise.domain.product.Product;

public record SaleStatusUpdateRequest(@NotBlank Product.SaleStatus saleStatus) {

}
