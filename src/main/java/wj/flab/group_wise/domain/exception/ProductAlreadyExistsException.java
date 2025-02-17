package wj.flab.group_wise.domain.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public static final String MESSAGE = "이미 존재하는 상품입니다.";
    public ProductAlreadyExistsException() {
        super(MESSAGE);
    }
}
