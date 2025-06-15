package wj.flab.group_wise.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    description = "회원가입 요청 정보",
    example = """
    {
        "email": "user@example.com",
        "password": "securePassword123",
        "address": "서울특별시 강남구 테헤란로 123"
    }
    """
)
public record MemberCreateRequest (
    @Schema(
        description = "이메일 주소 (로그인 아이디로 사용)",
        example = "user@example.com",
        format = "email",
        maxLength = 100
    )
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

    @Schema(
        description = "비밀번호 (8자 이상 권장)",
        example = "securePassword123",
        minLength = 6,
        maxLength = 50
    )
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    String password,

    @Schema(
        description = "주소 (배송지로 사용됩니다)",
        example = "서울특별시 강남구 테헤란로 123, 101동 1001호",
        maxLength = 200
    )
    @NotBlank(message = "주소는 필수입니다")
    String address
) { }
