package wj.flab.group_wise.domain.exception;

import org.springframework.http.HttpStatus;
import wj.flab.group_wise.exception.BusinessException;

public class AuthenticationFailedException extends BusinessException {

    public AuthenticationFailedException() {
        super(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.");
    }
}
