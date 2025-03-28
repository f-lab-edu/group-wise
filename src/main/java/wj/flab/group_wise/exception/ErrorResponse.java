package wj.flab.group_wise.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
        String message
) {
    static LocalDateTime timeStamp = LocalDateTime.now();
}
