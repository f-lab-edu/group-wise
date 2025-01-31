package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.domain.product.ProductStock;

@Entity
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

    @OneToOne(fetch = FetchType.LAZY)
    private ProductStock selectedProduct;   // 선택한 상품
    private Integer quantity;               // 구매 수량

    private boolean isWishlist;                 // 관심 여부 (찜)
    private boolean hasParticipated;            // 구매 참여 여부

}
