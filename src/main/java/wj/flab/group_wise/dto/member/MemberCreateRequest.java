package wj.flab.group_wise.dto.member;

import jakarta.validation.constraints.NotBlank;

public record MemberCreateRequest (
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String address
) { }
