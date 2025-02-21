package wj.flab.group_wise.domain.exception;

public class ProductStockNotFoundException extends RuntimeException {

    public ProductStockNotFoundException(Long id) {
        super(String.format("ID가 %d인 상품 옵션이 존재하지 않습니다.", id));
    }
}
