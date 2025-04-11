package wj.flab.group_wise.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import wj.flab.group_wise.exception.BusinessException;

@Getter
public class EntityNotFoundException extends BusinessException { // 404 에러

    private final TargetEntity entity;
    private final Long id;

    public EntityNotFoundException(TargetEntity entity, Long id) {
        super(
            HttpStatus.NOT_FOUND,
            String.format("ID가 %d인 %s(이)가 존재하지 않습니다.", id, entity.getName())
        );
        this.entity = entity;
        this.id = id;
    }

    public EntityNotFoundException(TargetEntity entity, String message) {
        super(HttpStatus.NOT_FOUND, message);
        this.entity = entity;
        this.id = null;
    }

}
