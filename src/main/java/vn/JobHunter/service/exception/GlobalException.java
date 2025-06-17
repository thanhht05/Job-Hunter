package vn.JobHunter.service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<String> handleResourceNotFoundException(IdInvalidException invalidException) {
        return ResponseEntity.badRequest().body(invalidException.getMessage());
    }
}