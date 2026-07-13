package com.fitlife.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifica que el contexto de Spring del Gateway (rutas, filtros, CORS)
 * se levanta correctamente sin errores de configuración.
 */
@SpringBootTest
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no levanta (ej. error en application.yml o en una ruta mal definida),
        // este test falla automáticamente.
    }
}
