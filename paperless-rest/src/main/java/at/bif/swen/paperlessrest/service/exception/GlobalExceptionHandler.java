package at.bif.swen.paperlessrest.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateDocumentNameException.class)
    public ResponseEntity<Map<String,String>> handleDuplicate(DuplicateDocumentNameException e){
        Map<String,String> body = Map.of("error", "duplicated name", "message", e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
