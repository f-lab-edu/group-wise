package wj.flab.group_wise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import wj.flab.group_wise.exception.ErrorResponse;
import wj.flab.group_wise.service.domain.MemberService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "🔐 인증 관리",
    description = "회원가입, 로그인, JWT 토큰 발급을 담당하는 API입니다."
)
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = """
            ### 📋 기능 설명
            - 이메일과 비밀번호로 로그인하여 JWT 액세스 토큰을 발급받습니다
            - 발급받은 토큰은 다른 API 호출 시 Authorization 헤더에 포함해야 합니다
            - 토큰 유효기간: 1시간
            
            ### 🔒 사용법
            1. 이메일과 비밀번호를 입력하여 로그인
            2. 응답으로 받은 JWT 토큰을 복사
            3. 우상단 'Authorize' 버튼 클릭 후 토큰 입력
            4. 이후 모든 API 호출에 자동으로 인증 헤더 추가됨
            
            ### ⚠️ 주의사항
            - 이메일 형식이 올바르지 않으면 400 에러
            - 존재하지 않는 이메일이면 401 에러
            - 비밀번호가 틀리면 401 에러
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 로그인 성공"),
        @ApiResponse(responseCode = "401", description = "❌ 로그인 실패")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "로그인 정보 (이메일, 비밀번호)",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MemberLoginRequest.class),
            examples = {
                @ExampleObject(
                    name = "일반 사용자 로그인",
                    summary = "테스트 계정으로 로그인",
                    description = "미리 가입된 테스트 계정으로 로그인하는 예시",
                    value = """
                    {
                        "email": "test@example.com",
                        "password": "password123"
                    }
                    """
                ),
                @ExampleObject(
                    name = "관리자 로그인",
                    summary = "관리자 계정으로 로그인",
                    value = """
                    {
                        "email": "admin@group-wise.com",
                        "password": "admin123"
                    }
                    """
                )
            }
        )
    )
    public ResponseEntity<JwtResponse> login(@RequestBody MemberLoginRequest memberLoginRequest) {
        JwtResponse response = memberService.login(memberLoginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(
        summary = "회원가입",
        description = """
            ### 📋 기능 설명
            - 새로운 회원을 등록합니다
            - 회원가입 후 바로 로그인하여 JWT 토큰을 받을 수 있습니다
            - 이메일은 중복될 수 없습니다 (유니크 제약)
            
            ### 📝 입력 규칙
            - **이메일**: 올바른 이메일 형식이어야 함 (예: user@example.com)
            - **비밀번호**: 최소 8자 이상 권장 (보안상 안전)
            - **주소**: 배송지로 사용될 주소 정보
            
            ### 🔄 후속 작업
            1. 회원가입 성공 후 응답의 `id` 값 확인
            2. 같은 이메일/비밀번호로 `/api/auth/login` 호출
            3. JWT 토큰 발급받아 다른 API 사용
            
            ### ⚠️ 주의사항
            - 이미 존재하는 이메일로 가입 시도하면 409 에러
            - 필수 필드 누락 시 400 에러
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "✅ 회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "❌ 이메일 중복")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "회원가입 정보 (이메일, 비밀번호, 주소)",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MemberCreateRequest.class),
            examples = {
                @ExampleObject(
                    name = "일반 사용자 가입",
                    summary = "개인 사용자 회원가입",
                    description = "공동구매에 참여할 일반 사용자 가입 예시",
                    value = """
                    {
                        "email": "user@example.com",
                        "password": "securePassword123",
                        "address": "서울특별시 강남구 테헤란로 123, 101동 1001호"
                    }
                    """
                ),
                @ExampleObject(
                    name = "판매자 가입",
                    summary = "상품 판매자 회원가입",
                    description = "공동구매 상품을 판매할 사업자 가입 예시",
                    value = """
                    {
                        "email": "seller@company.com",
                        "password": "businessPass456",
                        "address": "서울특별시 서초구 서초대로 396, 강남빌딩 15층"
                    }
                    """
                ),
                @ExampleObject(
                    name = "테스트 계정",
                    summary = "개발/테스트용 계정",
                    description = "API 테스트를 위한 더미 계정 생성 예시",
                    value = """
                    {
                        "email": "test@group-wise.com",
                        "password": "test123",
                        "address": "서울특별시 중구 을지로 100"
                    }
                    """
                )
            }
        )
    )
    public ResponseEntity<CreateResponse> register(
        @Parameter(
            name = "memberCreateRequest",
            description = "회원가입 정보 (이메일, 비밀번호, 주소)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MemberCreateRequest.class),
                examples = {
                    @ExampleObject(
                        name = "일반 사용자 가입",
                        summary = "개인 사용자 회원가입",
                        description = "공동구매에 참여할 일반 사용자 가입 예시",
                        value = """
                        {
                            "email": "user@example.com",
                            "password": "securePassword123",
                            "address": "서울특별시 강남구 테헤란로 123, 101동 1001호"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "판매자 가입",
                        summary = "상품 판매자 회원가입",
                        description = "공동구매 상품을 판매할 사업자 가입 예시",
                        value = """
                        {
                            "email": "seller@company.com",
                            "password": "businessPass456",
                            "address": "서울특별시 서초구 서초대로 396, 강남빌딩 15층"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "테스트 계정",
                        summary = "개발/테스트용 계정",
                        description = "API 테스트를 위한 더미 계정 생성 예시",
                        value = """
                        {
                            "email": "test@group-wise.com",
                            "password": "test123",
                            "address": "서울특별시 중구 을지로 100"
                        }
                        """
                    )
                }
            )
        )
        @RequestBody MemberCreateRequest memberCreateRequest) {
        Long memberId = memberService.registerMember(memberCreateRequest);

        URI location = ServletUriComponentsBuilder
            .fromPath("/api/members/{memberId}")
            .buildAndExpand(memberId)
            .toUri();

        CreateResponse response = new CreateResponse(memberId);
        return ResponseEntity.created(location).body(response);
    }

}
