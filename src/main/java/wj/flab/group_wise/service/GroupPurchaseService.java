package wj.flab.group_wise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseCreateRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseJoinRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseUpdateRequest;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;
import wj.flab.group_wise.repository.GroupPurchaseRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPurchaseService {

    private final ProductService productService;
    private final GroupPurchaseRepository groupPurchaseRepository;

    public Long createGroupPurchase(GroupPurchaseCreateRequest groupCreateRequest) {
        Long productId = groupCreateRequest.productId();
        Product product = productService.findProductById(productId);

        if (product.getSaleStatus() != SaleStatus.SALE) {
            throw new IllegalStateException("판매 중인 상품이 아닙니다.");
        }

        List<GroupPurchase> ongoingGroupsOfProduct = groupPurchaseRepository.findGroupPurchaseByProductAndStatus(Status.ONGOING, product.getId());
        if (!ongoingGroupsOfProduct.isEmpty()) {
            throw new IllegalStateException("해당 상품(productId=" + productId + ")에 대해서 이미 진행중인 그룹 구매가 있습니다.");
        }

        GroupPurchase groupPurchase = groupCreateRequest.toEntity();
        groupPurchaseRepository.save(groupPurchase);

        return groupPurchase.getId();
    }

    public void updateGroupPurchase(Long groupPurchaseId, GroupPurchaseUpdateRequest groupUpdateRequest) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);

        groupPurchase.updateGroupPurchaseInfo(
            groupUpdateRequest.title(),
            groupUpdateRequest.productId(),
            groupUpdateRequest.discountRate(),
            groupUpdateRequest.minimumParticipants(),
            groupUpdateRequest.startDate(),
            groupUpdateRequest.endDate()
        );
    }

    @Transactional(readOnly = true)
    public GroupPurchase findGroupPurchase(Long groupPurchaseId) {
        return groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.GROUP_PURCHASE, groupPurchaseId));
    }

    public void deleteGroupPurchase(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        if (!groupPurchase.isModifiable()) {
            throw new IllegalStateException("진행이 시작된 공동구매는 삭제할 수 없습니다.");
        }
        groupPurchaseRepository.delete(groupPurchase);
    }

    public void startGroupPurchase(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.start();
    }

    public void cancelGroupPurchase(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.cancel();

        // todo 추후 참여자에게 알림 기능 구현하기
    }

    public void joinGroupPurchase(Long groupPurchaseId, Long memberId, List<GroupPurchaseJoinRequest> joinRequests) {

        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        Product product = productService.findProductById(groupPurchase.getProductId());

        for (GroupPurchaseJoinRequest joinRequest : joinRequests) {
            Long stockId = joinRequest.productStockId();
            int quantity = joinRequest.quantity();
            product.decreaseStockQuantity(stockId, quantity);
            groupPurchase.addParticipant(memberId, stockId, quantity);
        }

        // todo 추후 참여자에게 최소 인원 달성 알림 기능 구현하기
    }

    public void modifyOrder(Long groupPurchaseId, Long memberId,
        List<? extends GroupPurchaseOrderModifyCommand> requests) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);

        for (GroupPurchaseOrderModifyCommand request : requests) {
            request.execute(groupPurchase, memberId);
        }
    }

    public void leaveGroupPurchase(Long groupPurchaseId, Long memberId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.removeParticipant(memberId);
        // todo 추후 참여자에게 알림 기능 구현하기
    }

    public void wishGroupPurchase(Long groupPurchaseId, Long memberId, boolean wish) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.wishGroupPurchase(memberId, wish);

    }
}
