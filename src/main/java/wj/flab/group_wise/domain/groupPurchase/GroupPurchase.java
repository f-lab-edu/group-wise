package wj.flab.group_wise.domain.groupPurchase;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
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
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.ProductStock;

@Entity
@NoArgsConstructor(access = PROTECTED)
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

    @OneToMany(
        mappedBy = "groupPurchase",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<GroupPurchaseParticipant> groupPurchaseParticipants = new ArrayList<>(); // 참여 회원

    @Enumerated(EnumType.STRING)
    private Status status;                                                                // 진행 상태

    public static GroupPurchase createGroupPurchase(String title, Product product, Integer discountRate, Integer initialPrice, Integer minimumParticipants, LocalDateTime startDate, LocalDateTime endDate) {
        GroupPurchase groupPurchase = new GroupPurchase();
        groupPurchase.title = title;
        groupPurchase.product = product;
        groupPurchase.discountRate = discountRate;
        groupPurchase.initialPrice = initialPrice;
        groupPurchase.minimumParticipants = minimumParticipants;
        groupPurchase.startDate = startDate;
        groupPurchase.endDate = endDate;
        groupPurchase.status = Status.PENDING;
        return groupPurchase;
    }

    public void updateGroupPurchaseInfo(String title, Product product, Integer discountRate, Integer initialPrice, Integer minimumParticipants, LocalDateTime startDate, LocalDateTime endDate) {
        if (status != Status.PENDING) {
            throw new IllegalStateException("진행 중인 공동구매는 수정할 수 없습니다.");
        }
        this.title = title;
        this.product = product;
        this.discountRate = discountRate;
        this.initialPrice = initialPrice;
        this.minimumParticipants = minimumParticipants;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void start() {
        if (status != Status.PENDING) {
            throw new IllegalStateException("이미 시작된 공동구매입니다.");
        }
        status = Status.ONGOING;
    }

    public void addParticipant(Member member, ProductStock selectedProduct, Integer quantity) {
        if (!canJoin()) {
            throw new IllegalStateException("현재 참여가 불가능한 공동구매입니다.");
        }

        if (isMinimumParticipantsMet()) {
            // todo 최소 인원 달성 이벤트 발생 -> 작성 위치가 맞으려나
        }

        // 동시성 문제는 없을까
        this.product.decreaseStockQuantity(selectedProduct.getId(), quantity);
        this.groupPurchaseParticipants.add(
            GroupPurchaseParticipant.createPurchaseParticipant(this, member, selectedProduct, quantity));
    }

    public Status complete() {
        if (status != Status.ONGOING) {
            throw new IllegalStateException("진행 중인 공동구매가 아닙니다.");
        }

        if (groupPurchaseParticipants.size() < minimumParticipants) {
            status = Status.COMPLETED_FAILURE;
        } else {
            status = Status.COMPLETED_SUCCESS;
        }

        // todo 완료 이벤트 발생
        // - 참여자에게 알림
        // - 성공시 주문 객체 생성

        return status;
    }

    public boolean canJoin() {
        return status == Status.ONGOING && LocalDateTime.now().isBefore(endDate);
    }

    public boolean isMinimumParticipantsMet() {
        return groupPurchaseParticipants.size() >= minimumParticipants;
    }

    public boolean isOngoing() {
        return status == Status.ONGOING;
    }

}
