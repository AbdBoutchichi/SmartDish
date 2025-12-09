package com.springbootTemplate.univ.soa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    /**
     * Gestion des exceptions de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put(STATUS_KEY, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR_KEY, "Erreur de validation");
        response.put("errors", errors);

        log.error("Erreur de validation: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gestion de l'exception RecetteNotFoundException
     */
    @ExceptionHandler(RecetteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRecetteNotFoundException(
            RecetteNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put(STATUS_KEY, HttpStatus.NOT_FOUND.value());
        response.put(ERROR_KEY, "Recette non trouvée");
        response.put(MESSAGE_KEY, ex.getMessage());

        log.error("Recette non trouvée: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Gestion de l'exception IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put(STATUS_KEY, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR_KEY, "Argument invalide");
        response.put(MESSAGE_KEY, ex.getMessage());

        log.error("Argument invalide: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gestion des exceptions génériques
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put(STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(ERROR_KEY, "Erreur interne du serveur");
        response.put(MESSAGE_KEY, ex.getMessage());

        log.error("Erreur interne: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Gestion des exceptions de communication inter-services
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put(STATUS_KEY, HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put(ERROR_KEY, "Service temporairement indisponible");
        response.put(MESSAGE_KEY, ex.getMessage());

        log.error("Erreur runtime: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

