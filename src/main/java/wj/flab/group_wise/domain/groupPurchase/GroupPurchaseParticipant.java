package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPurchaseParticipant extends BaseTimeEntity { // 공동구매 참여자와 구매 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_purchase_id")
    private GroupPurchase groupPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long productStockId;            // 선택한 상품
    private Integer quantity;               // 구매 수량

    private boolean isWishlist;                 // 관심 여부 (찜)
    private boolean hasParticipated;            // 구매 참여 여부

    private GroupPurchaseParticipant(
        GroupPurchase groupPurchase,
        Member member,
        Long productStockId,
        Integer quantity) {

        this.groupPurchase = groupPurchase;
        this.member = member;
        this.productStockId = productStockId;
        this.quantity = quantity;
        this.isWishlist = false;
        this.hasParticipated = true;
    }

    private GroupPurchaseParticipant(
        GroupPurchase groupPurchase,
        Member member,
        boolean isWishlist) {

        this.groupPurchase = groupPurchase;
        this.member = member;
        this.isWishlist = isWishlist;
    }

    protected static GroupPurchaseParticipant createPurchaseParticipant(
        GroupPurchase groupPurchase,
        Member member,
        Long productStockId,
        Integer quantity) {

        return new GroupPurchaseParticipant(groupPurchase, member, productStockId, quantity);
    }

    protected static GroupPurchaseParticipant createWishlistParticipant(GroupPurchase groupPurchase, Member member) {
        return new GroupPurchaseParticipant(groupPurchase, member, true);
    }


}
