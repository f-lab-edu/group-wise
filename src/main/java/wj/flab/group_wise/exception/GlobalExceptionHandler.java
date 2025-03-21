package wj.flab.group_wise.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("예측하지 못한 오류가 발생했습니다."));
    }

//    // 유효성 검사 예외 처리 --> @Valid 어노테이션을 사용한 경우 (AI)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
//        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
//            .map(error -> error.getField() + ": " + error.getDefaultMessage())
//            .collect(Collectors.joining(", "));
//
//        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", errorMessage);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//    }
}
