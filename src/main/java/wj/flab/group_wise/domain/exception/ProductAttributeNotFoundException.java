package wj.flab.group_wise.domain.exception;

public class ProductAttributeNotFoundException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 상품 속성이 존재하지 않습니다.";

    public ProductAttributeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ProductAttributeNotFoundException(Long id) {
        super(String.format("ID가 %d인 " + DEFAULT_MESSAGE, id));
    }
}
