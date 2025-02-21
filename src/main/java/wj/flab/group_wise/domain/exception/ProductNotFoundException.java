package wj.flab.group_wise.domain.exception;

public class ProductNotFoundException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 상품이 존재하지 않습니다.";

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Long id) {
        super(String.format("ID가 %d인 " + DEFAULT_MESSAGE, id));
    }
}
