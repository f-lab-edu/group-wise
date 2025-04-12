package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class GroupPurchaseItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_purchase_member_id")
    private GroupPurchaseMember groupPurchaseMember;

    private Long productStockId;                        // 선택한 상품
    private Integer quantity;                           // 구매 수량

    protected GroupPurchaseItem(GroupPurchaseMember groupPurchaseMember, Long productStockId, Integer quantity) {
        this.groupPurchaseMember = groupPurchaseMember;
        this.productStockId = productStockId;
        this.quantity = quantity;
    }
}
