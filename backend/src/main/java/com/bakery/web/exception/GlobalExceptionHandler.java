package com.bakery.web.exception;

import com.bakery.application.exception.EmployeeNotFoundException;
import com.bakery.application.exception.FixedCostNotFoundException;
import com.bakery.application.exception.InputNotFoundException;
import com.bakery.application.exception.ProductNotFoundException;
import com.bakery.application.exception.RecipeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Manejador global de errores. Respuesta uniforme:
 * { "timestamp", "status", "error", "message" }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(Instant timestamp, int status, String error, String message) {

        static ErrorResponse of(HttpStatus status, String message) {
            return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message);
        }
    }

    /** Recursos inexistentes -> 404. */
    @ExceptionHandler({
            InputNotFoundException.class,
            RecipeNotFoundException.class,
            ProductNotFoundException.class,
            FixedCostNotFoundException.class,
            EmployeeNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /** Validaciones de DTOs (@Valid) -> 400 con detalle por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, detail));
    }

    /** Reglas de dominio violadas (validaciones de constructores/métodos) -> 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /** Estado del negocio inválido para la operación (ej: settings sin configurar) -> 409. */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage()));
    }

    /** Cualquier otro error -> 500 sin filtrar detalles internos. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));
    }
}
