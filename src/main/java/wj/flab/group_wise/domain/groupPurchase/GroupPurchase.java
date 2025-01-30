package wj.flab.group_wise.domain.groupPurchase;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class
GroupPurchase { // 공동 구매 그룹

    @Getter
    @RequiredArgsConstructor
    public enum Status {  // GroupPurchaseStatus -> Status로 단순화
        PENDING("시작 전"),
        ONGOING("진행 중"),
        FULFILLED("최소 인원 달성"),
        COMPLETED_SUCCESS("목표 달성 완료"), // CLOSED_SUCCESS -> COMPLETED_SUCCESS
        COMPLETED_FAILURE("목표 미달 종료"), // CLOSED_FAILURE -> COMPLETED_FAILURE
        CANCELLED("중도 취소");             // CANCELED -> CANCELLED (영국식 영어 사용)

        private final String description;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;                // 공구 이름

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;            // 상품
    private Integer discountRate;           // 할인율
    private Integer initialPrice;           // 공구 (시작) 가격 (상품 원가와 다를 수 있음)
    private Integer minimumParticipants;    // 최소 진행 인원
    private Integer currentParticipants;    // 현재 참여 인원

    private LocalDateTime startDate;    // 시작일
    private LocalDateTime endDate;      // 종료일

    @OneToMany(mappedBy = "groupPurchase")// 참여 회원
    private List<GroupPurchaseParticipant> groupPurchaseParticipants = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status; // 상태 (대기, 진행중, 종료)

    @Embedded
    private BaseTimeEntity baseTimeEntity;

}
