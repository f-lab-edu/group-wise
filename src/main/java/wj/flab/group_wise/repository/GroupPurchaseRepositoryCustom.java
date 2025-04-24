package wj.flab.group_wise.repository;

import java.util.List;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseSearchRequest;

public interface GroupPurchaseRepositoryCustom {
    List<GroupPurchase> searchGroupPurchases(GroupPurchaseSearchRequest searchRequest);
}
