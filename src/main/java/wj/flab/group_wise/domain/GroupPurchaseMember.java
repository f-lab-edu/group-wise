package wj.flab.group_wise.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class GroupPurchaseMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GroupPurchase groupPurchase;

    @ManyToOne
    private Member member;

    private boolean isWish;             // 관심 여부
    private LocalDateTime joinDate;     // 공동구매 참여일
}
