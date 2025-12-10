package com.nttdata.bootcamp.handler;
import com.nttdata.bootcamp.exception.BadRequestException;
import com.nttdata.bootcamp.exception.ConflictException;
import com.nttdata.bootcamp.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Mono<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return Mono.just(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public Mono<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public Mono<Map<String, Object>> handleConflictException(ConflictException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        return buildError(ex.getStatus(), ex.getReason());

    }

    @ExceptionHandler(Exception.class)
    public Mono<Map<String, Object>> handleUnknownException(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}
