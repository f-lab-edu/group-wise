package wj.flab.group_wise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.repository.GroupPurchaseRepository;
import wj.flab.group_wise.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPurchaseService {

    private final ProductRepository productRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;

    public Long createGroupPurchase() {
        // todo
        return null;
    }

    public void updateGroupPurchase() {
        // todo
    }

    public void deleteGroupPurchase() {
        // todo
    }

    public void startGroupPurchase() {
        // todo
    }

    public void endGroupPurchase() {
        // todo
    }

    public void cancelGroupPurchase() {
        // todo
    }

    public void joinGroupPurchase() {
        // todo
    }

}
