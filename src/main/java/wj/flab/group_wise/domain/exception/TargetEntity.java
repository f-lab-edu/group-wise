package wj.flab.group_wise.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public enum TargetEntity {
    MEMBER("회원"),
    PRODUCT("상품"),
    PRODUCT_ATTRIBUTE("상품 속성"),
    PRODUCT_ATTRIBUTE_VALUE("상품 속성 값"),
    PRODUCT_STOCK("상품 재고"),
    GROUP_PURCHASE("공동구매"),
    GROUP_PURCHASE_MEMBER("공동구매 참여자"),
    GROUP_PURCHASE_ITEM("공동구매 상품");

    private final String name;
}
