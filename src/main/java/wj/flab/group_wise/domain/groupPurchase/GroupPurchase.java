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
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;

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
    private List<GroupPurchaseMember> groupPurchaseMembers = new ArrayList<>(); // 참여 회원

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

    public void addParticipant(Long memberId, long stockId, int quantity) {
        if (!canJoin()) {
            throw new IllegalStateException("현재 참여가 불가능한 공동구매입니다.");
        }

        GroupPurchaseMember member;
        Optional<GroupPurchaseMember> memberOptional =
            groupPurchaseMembers.stream().filter(m -> m.getMemberId().equals(memberId))
                .findFirst();

        if (memberOptional.isPresent()) {
            member = memberOptional.get();
        } else {
            member = GroupPurchaseMember.createParticipant(this, memberId);
            groupPurchaseMembers.add(member);
        }

        member.addGroupPurchaseItem(stockId, quantity);
    }

    private boolean canJoin() {
        return status == Status.ONGOING && LocalDateTime.now().isBefore(endDate);
    }

    public Status complete() {
        if (status != Status.ONGOING) {
            throw new IllegalStateException("진행 중인 공동구매가 아닙니다.");
        }

        if (groupPurchaseMembers.size() < minimumParticipants) {
            status = Status.COMPLETED_FAILURE;
        } else {
            status = Status.COMPLETED_SUCCESS;
        }

        // todo 성공시 주문 객체 생성
        return status;
    }

    public boolean isMinimumParticipantsMet() {
        return groupPurchaseMembers.size() >= minimumParticipants;
    }

    public boolean isModifiable() {
        return status == Status.PENDING;
    }

    public int getCurrentParticipants() {
        return groupPurchaseMembers.size();
    }

    public void wishGroupPurchase(Long memberId, boolean wish) {
        GroupPurchaseMember member;
        Optional<GroupPurchaseMember> memberOptional =
            groupPurchaseMembers.stream().filter(m -> m.getMemberId().equals(memberId))
                .findFirst();

        if (memberOptional.isPresent()) {
            member = memberOptional.get();
            member.setWishlist(wish);

        } else {
            member = GroupPurchaseMember.createWishlistParticipant(this, memberId);
            groupPurchaseMembers.add(member);
        }
    }

    public void removeParticipant(Long memberId) {
        GroupPurchaseMember participant = findParticipant(memberId);
        groupPurchaseMembers.remove(participant);
    }

    public void addItem(Long memberId, Long stockId, int quantity) {
        GroupPurchaseMember participant = findParticipant(memberId);
        participant.addGroupPurchaseItem(stockId, quantity);
    }

    public void updateItemQuantity(Long memberId, Long stockId, int quantity) {
        GroupPurchaseMember participant = findParticipant(memberId);
        participant.updateGroupPurchaseItem(stockId, quantity);
    }

    public void deleteOrder(Long memberId, Long stockId) {
        GroupPurchaseMember participant = findParticipant(memberId);
        participant.removeItem(stockId);

        if (participant.getSelectedItems().isEmpty() && !participant.isWishlist()) {
            groupPurchaseMembers.remove(participant);
        }
    }

    private GroupPurchaseMember findParticipant(Long memberId) {
        return groupPurchaseMembers.stream()
            .filter(participant ->
                participant.getMemberId().equals(memberId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                TargetEntity.GROUP_PURCHASE_MEMBER,
                String.format("memberId가 %d인 공동구매 참여자가 존재하지 않습니다.", memberId)
            ));
    }

    public List<Long> getParticipantIds() {
        return groupPurchaseMembers.stream()
            .filter(GroupPurchaseMember::isHasParticipated)
            .map(GroupPurchaseMember::getMemberId)
            .toList();
    }

    public List<Long> getWishlistIds() {
        return groupPurchaseMembers.stream()
            .filter(GroupPurchaseMember::isWishlist)
            .map(GroupPurchaseMember::getMemberId)
            .toList();
    }

}
