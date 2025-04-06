package wj.flab.group_wise.dto.gropPurchase;

import jakarta.validation.constraints.NotNull;

public record GroupPurchaseOrderModificationRequest(
    @NotNull GroupPurchaseOrderModificationRequest.RequestType requestType,
    @NotNull Long productStockId,
    Integer quantity
    ) {

    public GroupPurchaseOrderModificationRequest {
        if (requestType != RequestType.DELETE) {
            if (quantity == null) {
                throw new IllegalArgumentException("수량(quantity) 값은 null일 수 없습니다.");
            }

            if (quantity <= 0) {
                throw new IllegalArgumentException("수량(quantity) 값은 0보다 커야 합니다.");
            }
        }
    }

    public enum RequestType {
        ADD,
        QUANTITY_UPDATE,
        DELETE
    }
}
