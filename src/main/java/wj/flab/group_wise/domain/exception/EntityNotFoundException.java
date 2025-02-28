package wj.flab.group_wise.domain.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException { // 404 에러

    private final TargetEntity entity;
    private final Long id;

    public EntityNotFoundException(TargetEntity entity, Long id) {
        super(String.format("ID가 %d인 %s(이)가 존재하지 않습니다.", id, entity.getName()));
        this.entity = entity;
        this.id = id;
    }

}
