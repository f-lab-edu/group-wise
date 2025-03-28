package wj.flab.group_wise.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import wj.flab.group_wise.exception.BusinessException;

@Getter
public class AlreadyExistsException extends BusinessException {
    private final TargetEntity entity;

    public AlreadyExistsException(TargetEntity entity, String reason) {
        super(
            HttpStatus.CONFLICT,
            String.format("%s 중복 오류: %s", entity.getName(), reason)
        );
        this.entity = entity;
    }
}
