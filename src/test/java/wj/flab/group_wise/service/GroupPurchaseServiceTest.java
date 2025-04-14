package wj.flab.group_wise.service;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.Product.SaleStatus;
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.dto.groupPurchase.GroupPurchaseCreateRequest;
import wj.flab.group_wise.dto.groupPurchase.GroupPurchaseJoinRequest;
import wj.flab.group_wise.dto.groupPurchase.GroupPurchaseUpdateRequest;
import wj.flab.group_wise.dto.member.MemberCreateRequest;
import wj.flab.group_wise.dto.product.request.ProductStockSetRequest;
import wj.flab.group_wise.service.domain.GroupPurchaseService;
import wj.flab.group_wise.service.domain.MemberService;
import wj.flab.group_wise.service.domain.ProductService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupPurchaseServiceTest {

    @Autowired
    private GroupPurchaseService groupPurchaseService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDomainDtoCreator productDomainDtoCreator;
    @Autowired
    private MemberService memberService;

    @Test
    void createGroupPurchase() {
        // given
        Long productId = setAndGetProductId();

        setAndGetProductStockId(productId);

        // when : 공동구매 생성
        Long groupPurchaseId = setAndGetGroupPurchaseId(productId, "공동구매 1");

        // then : 공동구매 생성 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getTitle()).isEqualTo("공동구매 1");
    }

    private Long setAndGetGroupPurchaseId(Long productId, String title) {
        productService.updateProductDetails(productId, productDomainDtoCreator.createUpdateProductSaleStatusRequest(SaleStatus.SALE));
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(getGroupPurchaseCreateRequest(productId, title));
        return groupPurchaseId;
    }

    @Test
    void updateGroupPurchase() {
        // given
        Long productId = setAndGetProductId();
        Long groupPurchaseId = setAndGetGroupPurchaseId(productId, "공동구매 1");

        // when : 공동구매 수정
        groupPurchaseService.updateGroupPurchase(groupPurchaseId, getGroupUpdateRequest(productId, "공동구매 2"));

        // then : 공동구매 수정 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getTitle()).isEqualTo("공동구매 2");
    }

    @Test
    void deleteGroupPurchase() {
        // given
        Long productId = setAndGetProductId();
        Long groupPurchaseId = setAndGetGroupPurchaseId(productId, "공동구매");

        // when : 공동구매 삭제
        groupPurchaseService.deleteGroupPurchase(groupPurchaseId);

        // then : 공동구매 삭제 확인
        Assertions.assertThatThrownBy(() -> groupPurchaseService.findGroupPurchase(groupPurchaseId))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void startGroupPurchase() {
        // given
        Long productId = setAndGetProductId();
        Long groupPurchaseId = setAndGetGroupPurchaseId(productId, "공동구매");

        // when : 공동구매 시작
        groupPurchaseService.startGroupPurchase(groupPurchaseId);

        // then : 공동구매 시작 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getStatus()).isEqualTo(GroupPurchase.Status.ONGOING);
    }

    @Test
    void cancelGroupPurchase() {
        // given
        Long productId = setAndGetProductId();
        Long groupPurchaseId = setAndGetGroupPurchaseId(productId);

        // when : 공동구매 취소
        groupPurchaseService.cancelGroupPurchase(groupPurchaseId);

        // then : 공동구매 취소 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getStatus()).isEqualTo(GroupPurchase.Status.CANCELLED);
    }

    @Test
    void joinGroupPurchase() {
        // given
        Long productId = setAndGetProductId();
        Long stockId = setAndGetProductStockId(productId);
        Long groupPurchaseId = setAndGetGroupPurchaseId(productId);
        Long memberId = setAndGetMemberId();

        // when : 공동구매 참여
        groupPurchaseService.joinGroupPurchase(
            groupPurchaseId,
            memberId,
            List.of(new GroupPurchaseJoinRequest(stockId, 1)));

        // then : 공동구매 참여 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getCurrentParticipants()).isEqualTo(1);
    }

    private Long setAndGetProductId() {
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        Product product = productService.findProductById(productId);
        ProductStockSetRequest productStockSetRequest = productDomainDtoCreator.createStockToSet(productId, product.getProductStocks());
        productService.setProductStock(productId, productStockSetRequest);
        return productId;
    }

    private Long setAndGetProductStockId(Long productId) {
        Product product = productService.findProductById(productId);
        ProductStockSetRequest productStockSetRequest = productDomainDtoCreator.createStockToSet(productId, product.getProductStocks());
        productService.setProductStock(productId, productStockSetRequest);

        ProductStock stock = product.getProductStocks().get(0);
        return stock.getId();
    }

    private Long setAndGetMemberId() {
        return memberService.registerMember(
            new MemberCreateRequest("member1", "password1", "address"));
    }

    private Long setAndGetGroupPurchaseId(Long productId) {
        productService.updateProductDetails(productId, productDomainDtoCreator.createUpdateProductSaleStatusRequest(SaleStatus.SALE));
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(getGroupPurchaseCreateRequest(productId, "공동구매"));
        groupPurchaseService.startGroupPurchase(groupPurchaseId);
        return groupPurchaseId;
    }

    private static GroupPurchaseCreateRequest getGroupPurchaseCreateRequest(Long productId, String title) {
        return new GroupPurchaseCreateRequest(
            title,
            productId,
            10,
            10000,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    private static GroupPurchaseUpdateRequest getGroupUpdateRequest(Long productId, String title) {
        return new GroupPurchaseUpdateRequest(
            title,
            productId,
            20,
            20,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(4)
        );
    }
}