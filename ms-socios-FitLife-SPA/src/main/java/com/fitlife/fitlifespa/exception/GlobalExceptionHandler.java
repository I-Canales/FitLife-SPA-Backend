package com.fitlife.fitlifespa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejo centralizado de excepciones para todo ms-socios.
 * Traduce excepciones internas a respuestas HTTP consistentes en formato JSON,
 * evitando fugar stacktraces y devolviendo siempre un cuerpo estructurado
 * con timestamp, código, mensaje y (cuando aplica) errores de validación por campo.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado (Usuario, Membresía, etc.)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 401 - Credenciales inválidas en el login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(BadCredentialsException ex, WebRequest request) {
        log.warn("Intento de login con credenciales inválidas");
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", request, null);
    }

    // 400 - Errores de validación de Bean Validation (@Valid en los DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Error de validación en la petición: {}", errores);
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Los datos enviados son inválidos", request, errores);
    }

    // 400 - Argumentos ilegales de reglas de negocio (ej. estado inconsistente)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException ex, WebRequest request) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    // 500 - Cualquier otro error no controlado explícitamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex, WebRequest request) {
        log.error("Error interno no controlado", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor", request, null);
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje,
                                                                     WebRequest request, Map<String, String> errores) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        if (errores != null && !errores.isEmpty()) {
            body.put("errores", errores);
        }
        return ResponseEntity.status(status).body(body);
    }
}
