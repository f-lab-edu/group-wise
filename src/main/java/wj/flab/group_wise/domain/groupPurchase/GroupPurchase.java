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
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class GroupPurchase extends BaseTimeEntity {

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;                   // 공동구매 게시물 제목
    private Long productId;                 // 상품 ID

    private Integer discountRate;           // 할인율
    private Integer minimumParticipants;    // 최소 진행 인원

    private LocalDateTime startDate;        // 시작일
    private LocalDateTime endDate;          // 종료일

    @OneToMany(
        mappedBy = "groupPurchase",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Getter(PROTECTED)
    private List<GroupPurchaseParticipant> groupPurchaseParticipants = new ArrayList<>(); // 참여 회원

    @Enumerated(EnumType.STRING)
    private Status status;                                                                // 진행 상태

    public static GroupPurchase createGroupPurchase(String title, Long productId, Integer discountRate, Integer minimumParticipants, LocalDateTime startDate,
        LocalDateTime endDate) {
        GroupPurchase groupPurchase = new GroupPurchase();
        groupPurchase.title = title;
        groupPurchase.productId = productId;
        groupPurchase.discountRate = discountRate;
        groupPurchase.minimumParticipants = minimumParticipants;
        groupPurchase.startDate = startDate;
        groupPurchase.endDate = endDate;
        groupPurchase.status = Status.PENDING;
        return groupPurchase;
    }

    public void updateGroupPurchaseInfo(String title, Long productId, Integer discountRate, Integer minimumParticipants, LocalDateTime startDate, LocalDateTime endDate) {
        if (status != Status.PENDING) {
            throw new IllegalStateException("진행 중인 공동구매는 수정할 수 없습니다.");
        }
        if (title != null && !title.isEmpty()) {
            this.title = title;
        }
        if (productId != null) {
            this.productId = productId;
        }
        if (discountRate != null) {
            this.discountRate = discountRate;
        }
        if (minimumParticipants != null) {
            this.minimumParticipants = minimumParticipants;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    public void start() {
        if (status != Status.PENDING) {
            throw new IllegalStateException("이미 시작된 공동구매입니다.");
        }
        status = Status.ONGOING;
    }

    public void cancel() {
        if (status != Status.ONGOING) {
            throw new IllegalStateException("진행 중인 공동구매가 아닙니다.");
        }
        status = Status.CANCELLED;
    }

    public void addParticipant(Long memberId, Long productStockId, Integer quantity) {
        if (!canJoin()) {
            throw new IllegalStateException("현재 참여가 불가능한 공동구매입니다.");
        }

        this.groupPurchaseParticipants.add(
            GroupPurchaseParticipant.createPurchaseParticipant(this, memberId, productStockId, quantity));
    }

    private boolean canJoin() {
        return status == Status.ONGOING && LocalDateTime.now().isBefore(endDate);
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

    public boolean isMinimumParticipantsMet() {
        return groupPurchaseParticipants.size() >= minimumParticipants;
    }

    public boolean isModifiable() {
        return status == Status.PENDING;
    }

    public int getCurrentParticipants() {
        return groupPurchaseParticipants.size();
    }

    public void removeParticipant(Long memberId) {
        findParticipants(memberId).forEach(
            participant -> groupPurchaseParticipants.remove(participant)
        );
    }

    public void addOrder(Long memberId, Long stockId, int quantity) {
        addParticipant(memberId, stockId, quantity);
    }

    public void updateOrder(Long memberId, Long stockId, int quantity) {
        GroupPurchaseParticipant participant = findParticipant(memberId, stockId);
        participant.setQuantity(quantity);
    }

    public void deleteOrder(Long memberId, Long stockId) {
        GroupPurchaseParticipant participant = findParticipant(memberId, stockId);
        groupPurchaseParticipants.remove(participant);
    }

    public void wishGroupPurchase(Long memberId, boolean wish) {

        // 즐겨찾기는 공동구매 게시물 단위로 설정함 (stock 단위 아님)
        List<GroupPurchaseParticipant> participants = findParticipants(memberId);

        if (participants.isEmpty()) {
            // 즐겨찾기를 위해 생성
            GroupPurchaseParticipant participant = GroupPurchaseParticipant.createWishlistParticipant(this, memberId);
            this.groupPurchaseParticipants.add(participant);
        } else {
            // 이미 존재하는 참여자에 대해 즐겨찾기 설정
            participants.forEach(participant -> {
                participant.setWishlist(wish);
            });
        }
    }

    private GroupPurchaseParticipant findParticipant(Long memberId, Long productStockId) {
        return groupPurchaseParticipants.stream()
            .filter(participant ->
                participant.getMemberId().equals(memberId) && participant.getProductStockId().equals(productStockId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 참여자를 찾을 수 없습니다."));
    }

    private List<GroupPurchaseParticipant> findParticipants(Long memberId) {
        return groupPurchaseParticipants.stream()
            .filter(participant -> participant.getMemberId().equals(memberId))
            .toList();
    }



}
