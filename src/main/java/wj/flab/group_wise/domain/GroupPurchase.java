package wj.flab.group_wise.domain;

import jakarta.persistence.Column;
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
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupPurchase { // 공동 구매 그룹

    enum GroupPurchaseStatus {
        WAITING,    // 시작일 전
        PROGRESS,   // 진행중 (최소 진행 인원 미달성)
        SUCCESS,    // 진행중 (최소 진행 인원 달성)
        COMPLETED,  // 마감완료 (마감일까지 최소 진행 인원 달성하여 주문 완료)
        FAIL,       // 마감실패 (마감일까지 최소 진행 인원 미달성으로 인한 실패)
        CANCELED    // 중도취소
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;                // 공구 이름

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;            // 상품
    private int discountRate;           // 할인율
    private int purchasePrice;          // 공구 가격 (상품 원가와 다를 수 있음)
    private int minCount;               // 최소 진행 인원
    private int currentCount;           // 현재 참여 인원

    private LocalDateTime startDate;    // 시작일
    private LocalDateTime endDate;      // 종료일

    @OneToMany(mappedBy = "groupPurchase")// 참여 회원
    private List<GroupPurchaseMember> groupPurchaseMembers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GroupPurchaseStatus status; // 상태 (대기, 진행중, 종료)

    @Embedded
    private BaseTimeEntity baseTimeEntity;

}
