package wj.flab.group_wise.service;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.product.Product;
import wj.flab.group_wise.domain.product.ProductStock;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseCancelRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseCreateRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseDeleteRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseJoinRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseStartRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseUpdateRequest;
import wj.flab.group_wise.dto.member.MemberCreateRequest;
import wj.flab.group_wise.dto.product.ProductStockSetRequest;

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
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        GroupPurchaseCreateRequest groupCreateRequest = new GroupPurchaseCreateRequest(
            "공동구매 1",
            productId,
            10,
            10000,
            10,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        // when : 공동구매 생성
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupCreateRequest);

        // then : 공동구매 생성 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getTitle()).isEqualTo("공동구매 1");
    }

    @Test
    void updateGroupPurchase() {
        // given
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        GroupPurchaseCreateRequest groupCreateRequest = new GroupPurchaseCreateRequest(
            "공동구매 1",
            productId,
            10,
            10000,
            10,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupCreateRequest);

        // when : 공동구매 수정
        groupPurchaseService.updateGroupPurchase(new GroupPurchaseUpdateRequest(
            groupPurchaseId,
            "공동구매 2",
            productId,
            20,
            20000,
            20,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(4)
        ));

        // then : 공동구매 수정 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getTitle()).isEqualTo("공동구매 2");
    }

    @Test
    void deleteGroupPurchase() {
        // given
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        GroupPurchaseCreateRequest groupCreateRequest = new GroupPurchaseCreateRequest(
            "공동구매 1",
            productId,
            10,
            10000,
            10,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupCreateRequest);

        // when : 공동구매 삭제
        groupPurchaseService.deleteGroupPurchase(new GroupPurchaseDeleteRequest(groupPurchaseId));

        // then : 공동구매 삭제 확인
        Assertions.assertThatThrownBy(() -> groupPurchaseService.findGroupPurchase(groupPurchaseId))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void startGroupPurchase() {
        // given
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        GroupPurchaseCreateRequest groupCreateRequest = new GroupPurchaseCreateRequest(
            "공동구매 1",
            productId,
            10,
            10000,
            10,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupCreateRequest);

        // when : 공동구매 시작
        groupPurchaseService.startGroupPurchase(new GroupPurchaseStartRequest(groupPurchaseId));

        // then : 공동구매 시작 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getStatus()).isEqualTo(GroupPurchase.Status.ONGOING);
    }

    @Test
    void cancelGroupPurchase() {
        // given
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        GroupPurchaseCreateRequest groupCreateRequest = new GroupPurchaseCreateRequest(
            "공동구매 1",
            productId,
            10,
            10000,
            10,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupCreateRequest);
        groupPurchaseService.startGroupPurchase(new GroupPurchaseStartRequest(groupPurchaseId));

        // when : 공동구매 취소
        groupPurchaseService.cancelGroupPurchase(new GroupPurchaseCancelRequest(groupPurchaseId));

        // then : 공동구매 취소 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getStatus()).isEqualTo(GroupPurchase.Status.CANCELLED);
    }

    @Test
    void joinGroupPurchase() {
        // given
        Long productId = productService.createProduct(productDomainDtoCreator.createProductToCreate(1, 1));
        Product product = productService.findProduct(productId);
        ProductStockSetRequest productStockSetRequest = productDomainDtoCreator.createStockToSet(productId, product.getProductStocks());
        productService.setProductStock(productStockSetRequest);

        ProductStock stock = product.getProductStocks().get(0);
        GroupPurchaseCreateRequest groupCreateRequest = new GroupPurchaseCreateRequest(
            "공동구매 1",
            productId,
            10,
            10000,
            10,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupCreateRequest);
        groupPurchaseService.startGroupPurchase(new GroupPurchaseStartRequest(groupPurchaseId));
        Long memberId = memberService.createMember(
            new MemberCreateRequest("member1", "password1", "address"));

        // when : 공동구매 참여
        groupPurchaseService.joinGroupPurchase(
            new GroupPurchaseJoinRequest(groupPurchaseId, memberId, productId, stock.getId(), 1));

        // then : 공동구매 참여 확인
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        Assertions.assertThat(groupPurchase.getCurrentParticipants()).isEqualTo(1);
    }
}