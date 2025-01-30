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
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.product.Product;

@Entity
public class
GroupPurchase { // 공동 구매 그룹

    enum GroupPurchaseStatus {
        PENDING,        // 시작일 전
        ONGOING,        // 진행 중 (최소 인원 미달)
        FULFILLED,      // 진행 중 (최소 인원 달성)
        CLOSED_SUCCESS, // 마감 (목표 달성)
        CLOSED_FAILURE, // 마감 (목표 미달)
        CANCELED        // 중도취소
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;                // 공구 이름

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;            // 상품
    private Integer discountRate;           // 할인율
    private Integer purchasePrice;          // 공구 (시작) 가격 (상품 원가와 다를 수 있음)
    private Integer minCount;               // 최소 진행 인원
    private Integer currentCount;           // 현재 참여 인원

    private LocalDateTime startDate;    // 시작일
    private LocalDateTime endDate;      // 종료일

    @OneToMany(mappedBy = "groupPurchase")// 참여 회원
    private List<GroupPurchaseParticipant> groupPurchaseParticipants = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GroupPurchaseStatus status; // 상태 (대기, 진행중, 종료)

    @Embedded
    private BaseTimeEntity baseTimeEntity;

}
