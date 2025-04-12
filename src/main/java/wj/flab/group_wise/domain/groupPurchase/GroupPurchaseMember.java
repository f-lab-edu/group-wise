package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class GroupPurchaseMember extends BaseTimeEntity { // 공동구매 참여자와 구매 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_purchase_id")
    private GroupPurchase groupPurchase;

    private Long memberId;                          // 참여자 ID
    private boolean isWishlist;                     // 관심 여부 (찜)
    private boolean hasParticipated;                // 구매 참여 여부

    @OneToMany(
        mappedBy = "groupPurchaseMember",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<GroupPurchaseItem> selectedItems = new ArrayList<>();  // 구매 희망 상품 목록

    private GroupPurchaseMember(GroupPurchase groupPurchase, Long memberId) {
        this.groupPurchase = groupPurchase;
        this.memberId = memberId;
    }

    private GroupPurchaseMember(GroupPurchase groupPurchase, Long memberId, boolean isWishlist) {
        this(groupPurchase, memberId);
        this.isWishlist = isWishlist;
    }

    private GroupPurchaseMember(GroupPurchase groupPurchase, Long memberId, boolean isWishlist, boolean hasParticipated) {
        this(groupPurchase, memberId, isWishlist);
        this.hasParticipated = hasParticipated;
    }

    protected static GroupPurchaseMember createParticipant(GroupPurchase groupPurchase, Long memberId) {
        return new GroupPurchaseMember(groupPurchase, memberId, false, true);
    }

    protected static GroupPurchaseMember createWishlistParticipant(GroupPurchase groupPurchase, Long memberId) {
        return new GroupPurchaseMember(groupPurchase, memberId, true);
    }

    protected void addGroupPurchaseItem(Long productStockId, Integer quantity) {
        selectedItems.add(new GroupPurchaseItem(this, productStockId, quantity));
    }

    protected void updateGroupPurchaseItem(Long productStockId, Integer quantity) {
        GroupPurchaseItem groupPurchaseItem = findGroupPurchaseItem(productStockId);
        groupPurchaseItem.setQuantity(quantity);
    }

    protected void removeItem(Long stockId) {
        GroupPurchaseItem groupPurchaseItem = findGroupPurchaseItem(stockId);
        selectedItems.remove(groupPurchaseItem);
    }

    protected GroupPurchaseItem findGroupPurchaseItem(Long productStockId) {
        return selectedItems.stream()
            .filter(item -> item.getProductStockId().equals(productStockId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                TargetEntity.GROUP_PURCHASE_ITEM,
                String.format("productStockId가 %d인 공동구매 상품이 존재하지 않습니다.", productStockId)
            ));
    }


}
