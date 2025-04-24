package wj.flab.group_wise.domain.groupPurchase.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.dto.groupPurchase.request.order.AddOrderRequest;
import wj.flab.group_wise.dto.groupPurchase.request.order.DeleteOrderRequest;
import wj.flab.group_wise.dto.groupPurchase.request.order.ModifyOrderQuantityRequest;

/*
## use = JsonTypeInfo.Id.NAME:
    이 속성은 어떤 방식으로 타입 정보를 식별할지 지정합니다.
    JsonTypeInfo.Id.NAME은 클래스 이름(또는 지정된 이름)을 사용해 타입을 구분한다는 의미입니다.
    다른 옵션으로는 CLASS(완전한 클래스 이름), MINIMAL_CLASS(간소화된 클래스 이름), CUSTOM(사용자 정의) 등이 있습니다.

## include = JsonTypeInfo.As.PROPERTY:
    이 속성은 타입 정보를 어디에 포함시킬지 지정합니다.
    JsonTypeInfo.As.PROPERTY는 JSON 객체 내부에 추가 속성으로 타입 정보를 포함시킨다는 의미입니다.
    다른 옵션으로는 WRAPPER_ARRAY(배열로 감싸기), WRAPPER_OBJECT(객체로 감싸기), EXTERNAL_PROPERTY(외부 속성에 저장) 등이 있습니다.

## property = "@type" 속성은 타입 정보를 저장할 JSON 속성의 이름을 지정합니다.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AddOrderRequest.class, name = "AddOrderRequest"),
    @JsonSubTypes.Type(value = ModifyOrderQuantityRequest.class, name = "ModifyOrderQuantityRequest"),
    @JsonSubTypes.Type(value = DeleteOrderRequest.class, name = "DeleteOrderRequest")
})
public interface GroupPurchaseOrderModifyCommand {
    void execute(GroupPurchase groupPurchase, Long memberId);
}

