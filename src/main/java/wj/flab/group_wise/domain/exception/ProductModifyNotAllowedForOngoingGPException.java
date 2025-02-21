package wj.flab.group_wise.domain.exception;

public class ProductModifyNotAllowedForOngoingGPException extends RuntimeException {
    public static final String MESSAGE = "공동구매가 진행중인 상품은 수정할 수 없습니다.";
    public ProductModifyNotAllowedForOngoingGPException() {
        super(MESSAGE);
    }
}
