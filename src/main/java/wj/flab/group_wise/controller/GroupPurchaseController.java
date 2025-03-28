package wj.flab.group_wise.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import wj.flab.group_wise.service.GroupPurchaseService;

@Controller
@RequiredArgsConstructor
public class GroupPurchaseController {

    private final GroupPurchaseService groupPurchaseService;

}
