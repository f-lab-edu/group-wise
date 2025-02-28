package wj.flab.group_wise.domain.exception;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException { // 409 에러
    private final TargetEntity entity;

    public AlreadyExistsException(TargetEntity entity, String reason) {
        super(String.format("%s 중복 오류: %s", entity.getName(), reason));
        this.entity = entity;
    }
}
