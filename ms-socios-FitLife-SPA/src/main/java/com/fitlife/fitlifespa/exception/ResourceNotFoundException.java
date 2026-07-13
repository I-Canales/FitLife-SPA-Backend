package com.fitlife.fitlifespa.exception;

/**
 * Excepción lanzada cuando un recurso (Usuario, Membresía, etc.) no existe
 * en la base de datos. Es capturada de forma centralizada por
 * {@link GlobalExceptionHandler} y traducida a un HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
