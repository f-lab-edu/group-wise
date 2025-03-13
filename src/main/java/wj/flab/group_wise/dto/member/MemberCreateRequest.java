package wj.flab.group_wise.dto.member;

import jakarta.validation.constraints.NotBlank;
import wj.flab.group_wise.domain.Member;

public record MemberCreateRequest (
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String address
) {

    public Member toEntity() {
        return new Member(username, password, address);
    }
}
