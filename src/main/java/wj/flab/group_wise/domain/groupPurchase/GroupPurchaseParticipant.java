package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class GroupPurchaseParticipant extends BaseTimeEntity { // 공동구매 참여자와 구매 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_purchase_id")
    private GroupPurchase groupPurchase;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
    private Long memberId;                  // 참여자 ID

    private Long productStockId;            // 선택한 상품
    private Integer quantity;               // 구매 수량

    private boolean isWishlist;                 // 관심 여부 (찜)
    private boolean hasParticipated;            // 구매 참여 여부

    private GroupPurchaseParticipant(
        GroupPurchase groupPurchase,
        Long memberId,
        Long productStockId,
        Integer quantity) {

        this.groupPurchase = groupPurchase;
        this.memberId = memberId;
        this.productStockId = productStockId;
        this.quantity = quantity;
        this.isWishlist = false;
        this.hasParticipated = true;
    }

    private GroupPurchaseParticipant(
        GroupPurchase groupPurchase,
        Long memberId,
        boolean isWishlist) {

        this.groupPurchase = groupPurchase;
        this.memberId = memberId;
        this.isWishlist = isWishlist;
    }

    protected static GroupPurchaseParticipant createPurchaseParticipant(
        GroupPurchase groupPurchase,
        Long memberId,
        Long productStockId,
        Integer quantity) {

        return new GroupPurchaseParticipant(groupPurchase, memberId, productStockId, quantity);
    }

    protected static GroupPurchaseParticipant createWishlistParticipant(GroupPurchase groupPurchase, Long memberId) {
        return new GroupPurchaseParticipant(groupPurchase, memberId, true);
    }

}
