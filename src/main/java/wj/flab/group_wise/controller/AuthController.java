package wj.flab.group_wise.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wj.flab.group_wise.dto.CreateResponse;
import wj.flab.group_wise.dto.JwtResponse;
import wj.flab.group_wise.dto.member.MemberCreateRequest;
import wj.flab.group_wise.dto.member.MemberLoginRequest;
import wj.flab.group_wise.service.MemberService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody MemberLoginRequest memberLoginRequest) {
        JwtResponse response = memberService.login(memberLoginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<CreateResponse> register(@RequestBody MemberCreateRequest memberCreateRequest) {
        Long memberId = memberService.registerMember(memberCreateRequest);

        URI location = ServletUriComponentsBuilder
            .fromPath("/api/members/{memberId}")
            .buildAndExpand(memberId)
            .toUri();

        CreateResponse response = new CreateResponse(memberId);
        return ResponseEntity.created(location).body(response);
    }

}
