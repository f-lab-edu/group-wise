package wj.flab.group_wise.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.dto.member.MemberResponse;
import wj.flab.group_wise.service.MemberService;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    MemberResponse getMember(@PathVariable("id") Long id) {
        Member member = memberService.findMember(id);
        return MemberResponse.from(member);
    }
}
