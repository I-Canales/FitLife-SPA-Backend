package com.fitlife.fitlifespa.exception;

/**
 * Se lanza cuando la comunicación remota con ms-socios falla: el usuario
 * no existe/no está activo, o el servicio remoto no responde (timeout,
 * conexión rechazada, error 5xx). Permite distinguir un fallo de
 * interoperabilidad entre microservicios de un simple 404 local.
 */
public class ServicioRemotoException extends RuntimeException {
    public ServicioRemotoException(String mensaje) {
        super(mensaje);
    }

    public ServicioRemotoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
