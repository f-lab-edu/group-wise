package wj.flab.group_wise.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseCancelRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseCreateRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseDeleteRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseJoinRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseStartRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseUpdateRequest;
import wj.flab.group_wise.repository.GroupPurchaseRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPurchaseService {

    private final ProductService productService;
    private final MemberService memberService;
    private final GroupPurchaseRepository groupPurchaseRepository;

    public Long createGroupPurchase(GroupPurchaseCreateRequest groupCreateRequest) {
        Long productId = groupCreateRequest.productId();
        Product product = productService.findProduct(productId);
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

    public void updateGroupPurchase(GroupPurchaseUpdateRequest groupUpdateRequest) {
        GroupPurchase groupPurchase = findGroupPurchase(groupUpdateRequest.groupPurchaseId());

        groupPurchase.updateGroupPurchaseInfo(
            groupUpdateRequest.title(),
            groupUpdateRequest.productId(),
            groupUpdateRequest.discountRate(),
            groupUpdateRequest.initialPrice(),
            groupUpdateRequest.minimumParticipants(),
            groupUpdateRequest.startDate(),
            groupUpdateRequest.endDate()
        );
    }

    public GroupPurchase findGroupPurchase(Long groupPurchaseId) {
        return groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.GROUP_PURCHASE, groupPurchaseId));
    }

    public void deleteGroupPurchase(GroupPurchaseDeleteRequest groupDeleteRequest) {
        GroupPurchase groupPurchase = findGroupPurchase(groupDeleteRequest.groupPurchaseId());
        if (!groupPurchase.isModifiable()) {
            throw new IllegalStateException("진행이 시작된 공동구매는 삭제할 수 없습니다.");
        }
        groupPurchaseRepository.delete(groupPurchase);
    }

    public void startGroupPurchase(GroupPurchaseStartRequest groupStartRequest) {
        GroupPurchase groupPurchase = findGroupPurchase(groupStartRequest.groupPurchaseId());
        groupPurchase.start();
    }

    public void cancelGroupPurchase(GroupPurchaseCancelRequest groupCancelRequest) {
        GroupPurchase groupPurchase = findGroupPurchase(groupCancelRequest.groupPurchaseId());
        groupPurchase.cancel();

        // todo 추후 참여자에게 알림 기능 구현하기
    }

    public void joinGroupPurchase(GroupPurchaseJoinRequest groupJoinRequest) {

        GroupPurchase groupPurchase = findGroupPurchase(groupJoinRequest.groupPurchaseId());
        Member member = memberService.findMember(groupJoinRequest.memberId());
        Product product = productService.findProduct(groupJoinRequest.productId());
        Long stockId = groupJoinRequest.productStockId();
        int quantity = groupJoinRequest.quantity();

        // todo 추후 참여자에게 최소 인원 달성 알림 기능 구현하기

        product.decreaseStockQuantity(stockId, quantity);
        groupPurchase.addParticipant(member, stockId, quantity);
    }

}
