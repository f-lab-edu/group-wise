package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.product.Product;

@Entity
public class GroupPurchase extends BaseTimeEntity { // 공동구매 그룹

    @Getter
    @RequiredArgsConstructor
    public enum Status {  // GroupPurchaseStatus -> Status로 단순화
        PENDING("시작 전"),
        ONGOING("진행 중"),
//        FULFILLED("최소 인원 달성"),
        COMPLETED_SUCCESS("목표 달성 완료"),
        COMPLETED_FAILURE("목표 미달 종료"),
        CANCELLED("중도 취소");

        private final String description;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;                   // 공동구매 게시물 제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;                // 상품
    private Integer discountRate;           // 할인율
    private Integer initialPrice;           // 공구 가격 (할인율이 적용된, 옵션이 최종구성된 상품 가격 중 최소금액)
    private Integer minimumParticipants;    // 최소 진행 인원
//    private Integer currentParticipants;    // 현재 참여 인원

    private LocalDateTime startDate;        // 시작일
    private LocalDateTime endDate;          // 종료일

    @OneToMany(mappedBy = "groupPurchase")  // 참여 회원
    private List<GroupPurchaseParticipant> groupPurchaseParticipants = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;                  // 진행 상태

    public boolean isMinimumParticipantsMet() {
        return groupPurchaseParticipants.size() >= minimumParticipants;
    }

    public boolean isOngoing() {
        return status == Status.ONGOING;
    }

    public boolean canJoin() {
        return status == Status.ONGOING && LocalDateTime.now().isBefore(endDate);
    }

    // todo
    public void addParticipant() {
/*        if (!canJoin()) {
            throw new IllegalStateException("현재 참여가 불가능한 공동구매입니다.");
        }
        if (isMinimumParticipantsMet()) {
            // 최소 인원 달성 이벤트 발생
        }*/
    }

}
