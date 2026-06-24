package com.abdoul.hotel.Exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidException (MethodArgumentNotValidException ex){
        Map<String, String> response = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error-> {
            response.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeExceptions (RuntimeException ex){
        Map<String, String> response = new LinkedHashMap<>();

        ResponseStatus annotation = ex.getClass().getAnnotation(ResponseStatus.class);

        HttpStatus status = (annotation != null) ? annotation.value() : HttpStatus.BAD_REQUEST;

        response.put("error", ex.getMessage());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler (Exception.class)
    public ResponseEntity<Map<String, String>> handleUncatchedExceptions (Exception ex){
        Map<String, String> response = new LinkedHashMap<>();

        log.error("uncatched exception {}", ex.getMessage(), ex);

        response.put("error", "Something went wrong");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
