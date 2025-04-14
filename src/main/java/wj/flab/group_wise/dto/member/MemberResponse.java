package wj.flab.group_wise.dto.member;

import wj.flab.group_wise.domain.Member;

public record MemberResponse(
    Long id,
    String email,
    String address
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getEmail(),
            member.getAddress());
    }
}
