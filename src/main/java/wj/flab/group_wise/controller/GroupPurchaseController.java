package wj.flab.group_wise.controller;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.command.GroupPurchaseOrderModifyCommand;
import wj.flab.group_wise.dto.CreateResponse;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseCreateRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseJoinRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseUpdateRequest;
import wj.flab.group_wise.dto.gropPurchase.GroupPurchaseWishRequest;
import wj.flab.group_wise.dto.gropPurchase.order.AddOrderRequest;
import wj.flab.group_wise.dto.gropPurchase.order.DeleteOrderRequest;
import wj.flab.group_wise.dto.gropPurchase.order.ModifyOrderQuantityRequest;
import wj.flab.group_wise.service.GroupPurchaseService;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseController {

    private final GroupPurchaseService groupPurchaseService;

    @GetMapping("/{groupPurchaseId}")
    public ResponseEntity<GroupPurchase> getGroupPurchase(@PathVariable Long groupPurchaseId) {
        GroupPurchase groupPurchase = groupPurchaseService.findGroupPurchase(groupPurchaseId);
        return ResponseEntity.ok(groupPurchase);
    }

    @PostMapping
    public ResponseEntity<CreateResponse> createGroupPurchase(@RequestBody GroupPurchaseCreateRequest groupPurchaseCreateRequest) {
        Long groupPurchaseId = groupPurchaseService.createGroupPurchase(groupPurchaseCreateRequest);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{groupPurchaseId}")
            .buildAndExpand(groupPurchaseId)
            .toUri();

        CreateResponse response = new CreateResponse(groupPurchaseId);
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{groupPurchaseId}")
    public ResponseEntity<Void> deleteGroupPurchase(@PathVariable Long groupPurchaseId) {
        groupPurchaseService.deleteGroupPurchase(groupPurchaseId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupPurchaseId}")
    public ResponseEntity<Void> updateGroupPurchase(@PathVariable Long groupPurchaseId, @RequestBody GroupPurchaseUpdateRequest groupPurchaseUpdateRequest) {
        groupPurchaseService.updateGroupPurchase(groupPurchaseId, groupPurchaseUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupPurchaseId}/start")
    public ResponseEntity<Void> startGroupPurchase(@PathVariable Long groupPurchaseId) {
        groupPurchaseService.startGroupPurchase(groupPurchaseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupPurchaseId}/cancel")
    public ResponseEntity<Void> cancelGroupPurchase(@PathVariable Long groupPurchaseId) {
        groupPurchaseService.cancelGroupPurchase(groupPurchaseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupPurchaseId}/participants/{memberId}/join")
    public ResponseEntity<Void> joinGroupPurchase(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId,
        @RequestBody List<GroupPurchaseJoinRequest> joinRequests) {

        groupPurchaseService.joinGroupPurchase(groupPurchaseId, memberId, joinRequests);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupPurchaseId}/participants/{memberId}/leave")
    public ResponseEntity<Void> leaveGroupPurchase(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId) {

        groupPurchaseService.leaveGroupPurchase(groupPurchaseId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupPurchaseId}/participants/{memberId}/wish")
    public ResponseEntity<Void> wishGroupPurchase(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId,
        @RequestBody GroupPurchaseWishRequest wishRequest
    ) {

        groupPurchaseService.wishGroupPurchase(groupPurchaseId, memberId, wishRequest.wish());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupPurchaseId}/participants/{memberId}/orders")
    public ResponseEntity<Void> addOrder(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId,
        @RequestBody List<AddOrderRequest> requests) {

        groupPurchaseService.modifyOrder(groupPurchaseId, memberId, requests);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupPurchaseId}/participants/{memberId}/orders")
    public ResponseEntity<Void> modifyOrder(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId,
        @RequestBody List<ModifyOrderQuantityRequest> requests) {

        groupPurchaseService.modifyOrder(groupPurchaseId, memberId, requests);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupPurchaseId}/participants/{memberId}/orders")
    public ResponseEntity<Void> deleteOrder(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId,
        @RequestBody List<DeleteOrderRequest> requests) {

        groupPurchaseService.modifyOrder(groupPurchaseId, memberId, requests);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupPurchaseId}/participants/{memberId}/orders/batch")
    public ResponseEntity<Void> modifyOrderBatch(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long memberId,
        @RequestBody List<GroupPurchaseOrderModifyCommand> requests) {

        groupPurchaseService.modifyOrder(groupPurchaseId, memberId, requests);
        return ResponseEntity.noContent().build();
    }
}
