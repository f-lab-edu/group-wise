package wj.flab.group_wise.service.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchaseResponseMapper;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;
import wj.flab.group_wise.domain.groupPurchase.event.GroupPurchaseStartedEvent;
import wj.flab.group_wise.domain.groupPurchase.event.MinimumParticipantsMetEvent;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.domain.product.ProductViewResponseMapper;
import wj.flab.group_wise.dto.SortDirection;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseCreateRequest;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseJoinRequest;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseSearchRequest;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseUpdateRequest;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseDetailResponse;
import wj.flab.group_wise.dto.groupPurchase.response.GroupPurchaseSummaryResponse;
import wj.flab.group_wise.dto.product.response.ProductViewResponse;
import wj.flab.group_wise.repository.GroupPurchaseRepository;
import wj.flab.group_wise.service.event.GroupPurchaseEventPublisher;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupPurchaseService {

    private final ProductService productService;
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final ProductViewResponseMapper productMapper;
    private final GroupPurchaseResponseMapper groupPurchaseMapper;
    private final GroupPurchaseEventPublisher groupPurchaseEventPublisher;

    @Transactional(readOnly = true)
    public GroupPurchaseDetailResponse getGroupPurchaseDetail(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        Product product = productService.findProductById(groupPurchase.getProductId());

        // 기본정보 매핑
        GroupPurchaseDetailResponse baseResponse = groupPurchaseMapper.mapToBaseResponse(groupPurchase);

        // 추가정보 - 상품
        ProductViewResponse productResponse = productMapper.mapToProductViewResponse(product);

        // 추가정보 - 공동구매 시작가
        int discountedPrice = getMinPriceOfGroupPurchase(product.getId(), groupPurchase);

        // 추가정보 - 상품 옵션
        List<GroupPurchaseDetailResponse.GroupPurchaseStockResponse> groupPurchaseStockResponses
            = groupPurchaseMapper.mapToProductStockResponse(groupPurchase, product.getProductStocks());

        // 조합
        return baseResponse.withInfo(productResponse, discountedPrice, groupPurchaseStockResponses);
    }

    private int getMinPriceOfGroupPurchase(Long productId, GroupPurchase groupPurchase) {
        int cheapestStockPrice = productService.getCheapestStockPrice(productId);
        return calculateDiscountedPrice(cheapestStockPrice, groupPurchase.getDiscountRate());
    }

    private int calculateDiscountedPrice(int originalPrice, int discountRate) {
        return originalPrice - (originalPrice * discountRate / 100);
    }

    @Transactional(readOnly = true)
    public Page<GroupPurchaseSummaryResponse> searchGroupPurchases(GroupPurchaseSearchRequest searchRequest) {
        List<GroupPurchase> groupPurchases = groupPurchaseRepository.searchGroupPurchases(searchRequest);

        // 복잡한 정렬 조건은 애플리케이션에서 처리
        if (searchRequest.sortBy() != null) {
            switch (searchRequest.sortBy()) {
                case REMAINING_TIME:
                    sortByRemainingTime(groupPurchases, searchRequest.sortDirection());
                    break;
                case PARTICIPANT_COUNT:
                    sortByParticipantCount(groupPurchases, searchRequest.sortDirection());
                    break;
                case PARTICIPATION_RATE:
                    sortByParticipationRate(groupPurchases, searchRequest.sortDirection());
                    break;
                case CHEAPEST_PRICE:
                    sortByCheapestPrice(groupPurchases, searchRequest.sortDirection());
            }
        }

        List<GroupPurchaseSummaryResponse> allResults = groupPurchases.stream()
            .map(groupPurchase -> {
                Long productId = groupPurchase.getProductId();
                int minPriceOfGroupPurchase = getMinPriceOfGroupPurchase(productId, groupPurchase); // 공동구매 시작가 todo 엔티티 필드에 추가 고려
                Product product = productService.findProductById(productId);

                return groupPurchaseMapper.mapToSummaryResponse(groupPurchase, product, minPriceOfGroupPurchase);
            })
            .toList();

        return PageableExecutionUtils.getPage(
            allResults,
            PageRequest.of(searchRequest.page(), searchRequest.size()),
            allResults::size);
    }

    private void sortByCheapestPrice(List<GroupPurchase> allResults, SortDirection sortDirection) {
        Comparator<GroupPurchase> comparator = Comparator.comparing(
            gp -> productService.getCheapestStockPrice(gp.getProductId())
        );

        if (sortDirection == SortDirection.DESC) {
            comparator = comparator.reversed();
        }

        allResults.sort(comparator);
    }

    private void sortByRemainingTime(List<GroupPurchase> results, SortDirection direction) {
        LocalDateTime now = LocalDateTime.now();

        Comparator<GroupPurchase> comparator = Comparator.comparing(
            gp -> Duration.between(now, gp.getEndDate()).getSeconds()
        );

        if (direction == SortDirection.DESC) {
            comparator = comparator.reversed();
        }

        results.sort(comparator);
    }

    // 참여자 수로 정렬
    private void sortByParticipantCount(List<GroupPurchase> results, SortDirection direction) {
        Comparator<GroupPurchase> comparator = Comparator.comparing(
            GroupPurchase::getCurrentParticipantCount
        );

        if (direction == SortDirection.DESC) {
            comparator = comparator.reversed();
        }

        results.sort(comparator);
    }

    // 참여율로 정렬
    private void sortByParticipationRate(List<GroupPurchase> results, SortDirection direction) {
        Comparator<GroupPurchase> comparator = Comparator.comparing(
            GroupPurchase::getParticipationRate
        );

        if (direction == SortDirection.DESC) {
            comparator = comparator.reversed();
        }

        results.sort(comparator);
    }

    public Long createGroupPurchase(GroupPurchaseCreateRequest groupCreateRequest) {
        Long productId = groupCreateRequest.productId();
        Product product = productService.findProductById(productId);

        if (product.getSaleStatus() != SaleStatus.SALE) {
            throw new IllegalStateException("판매 중인 상품이 아닙니다.");
        }

        List<GroupPurchase> ongoingGroupsOfProduct = groupPurchaseRepository.findGroupPurchaseByProductAndStatus(Status.ONGOING, product.getId());
        if (!ongoingGroupsOfProduct.isEmpty()) {
            throw new IllegalStateException("해당 상품(productId=" + productId + ")에 대해서 이미 진행중인 그룹 구매가 있습니다.");
        }

        GroupPurchase groupPurchase = groupCreateRequest.toEntity();
        groupPurchaseRepository.save(groupPurchase);

        return groupPurchase.getId();
    }

    public void updateGroupPurchase(Long groupPurchaseId, GroupPurchaseUpdateRequest groupUpdateRequest) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);

        groupPurchase.updateGroupPurchaseInfo(
            groupUpdateRequest.title(),
            groupUpdateRequest.productId(),
            groupUpdateRequest.discountRate(),
            groupUpdateRequest.minimumParticipants(),
            groupUpdateRequest.startDate(),
            groupUpdateRequest.endDate()
        );
    }

    @Transactional(readOnly = true)
    public GroupPurchase findGroupPurchase(Long groupPurchaseId) {
        return groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.GROUP_PURCHASE, groupPurchaseId));
    }

    public void deleteGroupPurchase(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        if (!groupPurchase.isModifiable()) {
            throw new IllegalStateException("진행이 시작된 공동구매는 삭제할 수 없습니다.");
        }
        groupPurchaseRepository.delete(groupPurchase);
    }

    public void startGroupPurchase(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.start();

        groupPurchaseEventPublisher.publishStartEvent(
            new GroupPurchaseStartedEvent(this, groupPurchase));
    }

    public void cancelGroupPurchase(Long groupPurchaseId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.cancel();

        // todo 추후 참여자에게 알림 기능 구현하기
    }

    public void joinGroupPurchase(Long groupPurchaseId, Long memberId, List<GroupPurchaseJoinRequest> joinRequests) {

        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        Product product = productService.findProductById(groupPurchase.getProductId());

        for (GroupPurchaseJoinRequest joinRequest : joinRequests) {
            Long stockId = joinRequest.productStockId();
            int quantity = joinRequest.quantity();
            product.decreaseStockQuantity(stockId, quantity);
            groupPurchase.addParticipant(memberId, stockId, quantity);
        }

        if (groupPurchase.isMinimumParticipantsMet()) {
            groupPurchaseEventPublisher.publishMinimumParticipantsMetEvent(
                new MinimumParticipantsMetEvent(this, groupPurchase)
            );
        }
    }

    public void modifyOrder(Long groupPurchaseId, Long memberId,
        List<? extends GroupPurchaseOrderModifyCommand> requests) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);

        for (GroupPurchaseOrderModifyCommand request : requests) {
            request.execute(groupPurchase, memberId);
        }
    }

    public void leaveGroupPurchase(Long groupPurchaseId, Long memberId) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.removeParticipant(memberId);

        // todo 추후 참여자에게 알림 기능 구현하기
    }

    public void wishGroupPurchase(Long groupPurchaseId, Long memberId, boolean wish) {
        GroupPurchase groupPurchase = findGroupPurchase(groupPurchaseId);
        groupPurchase.wishGroupPurchase(memberId, wish);
    }
}
