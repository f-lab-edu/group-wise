package wj.flab.group_wise.repository;

import org.springframework.data.domain.Page;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseSearchRequest;

public interface GroupPurchaseRepositoryCustom {
    Page<GroupPurchase> searchGroupPurchases(GroupPurchaseSearchRequest searchRequest);
}
