package wj.flab.group_wise.domain.exception;

public class ProductAttributeValueAlreadyExistsException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "이미 존재하는 상품 속성입니다.";
    public ProductAttributeValueAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
    public ProductAttributeValueAlreadyExistsException(String attributeValueName) {
        super(attributeValueName + "은 " + DEFAULT_MESSAGE);
    }
}