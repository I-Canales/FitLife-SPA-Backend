FitLife SPA — Backend de Microservicios
Este es el proyecto que desarrollamos para el Examen Final Transversal de Desarrollo FullStack 1 (DSY1103). Básicamente, es el sistema de gestión para un gimnasio y está dividido en 3 aplicaciones independientes usando Spring Boot.

Cómo está organizado el sistema
El proyecto se divide en tres partes principales que trabajan en conjunto:

ms-socios: Es el servicio que maneja todo lo relacionado con los usuarios y las membresías. También se encarga de la seguridad del sistema mediante autenticación con JWT y Spring Security.

ms-entrenamientos: Este módulo gestiona los planes de entrenamiento. Tiene una particularidad: antes de crear cualquier plan, se comunica de forma interna con ms-socios a través de WebClient para revisar que el usuario realmente exista y esté activo.

api-gateway: Es la puerta de entrada única del proyecto desarrollada con Spring Cloud Gateway, en lugar de pegarle a cada servicio por separado, el Gateway recibe todas las peticiones en el puerto 8080, las redirige a donde corresponde y maneja temas globales como los CORS y el registro de tráfico.

Pasos para levantar el proyecto
Tenemos dos formas de poner a correr el sistema según lo que te acomode más:

parte 1: Ejecutarlo de forma local desde tu IDE
Como cada módulo es independiente, necesitas abrir tres terminales diferentes y ejecutar los comandos en este orden para que no tengas problemas de conexión:

Primero, entramos a la carpeta de socios y levantamos el servicio en el puerto 8081:
cd ms-socios-FitLife-SPA y luego mvn spring-boot:run

Segundo, abrimos otra terminal para el servicio de entrenamientos en el puerto 8082:
cd ms-entrenamientos-FitLife-SPA y luego mvn spring-boot:run

Tercero, levantamos el Gateway en el puerto 8080 para unificar todo:
cd api-gateway-FitLife-SPA y luego mvn spring-boot:run

Una recomendación: aunque puedes hacer pruebas apuntando directo a los puertos de debug 8081 o 8082, lo ideal es que hagas todas tus peticiones a través del Gateway en el puerto 8080. Por ejemplo, usando rutas como http://localhost:8080/api/usuarios

parte 2: Usar Docker Compose
Si quieres levantar todo el ecosistema de un solo golpe, puedes pararte en la raíz del proyecto y ejecutar:
docker compose up --build

Esto creará una red interna de Docker donde los servicios de entrenamientos y el gateway se comunicarán con el de socios usando directamente su nombre de servicio, olvidándonos del localhost

Rutas y Endpoints Disponibles
Para interactuar con la aplicación a través del Gateway con puerto 8080, preparamos los siguientes accesos:

Módulo de Socios
POST /api/auth/login: Te permite iniciar sesión y te devuelve el token JWT para rutas protegidas

GET /api/usuarios: Muestra el listado completo de los usuarios registrados

GET /api/usuarios/{id}: Obtiene el detalle de un usuario específico

POST /api/usuarios: Registra un usuario nuevo en el sistema

PUT /api/usuarios/{id}/desactivar: Cambia el estado de un usuario a inactivo

GET /api/usuarios/{id}/existe: Es un endpoint de consumo interno para comprobar la existencia del usuario antes de asignarle entrenamientos

GET /api/membresias: Lista las membresías vigentes Requiere token JWT

POST /api/membresias: Agrega una membresía nueva Requiere token JWT

DELETE /api/membresias/{id}: Elimina una membresía del sistema Requiere token JWT

Módulo de Entrenamientos

GET /api/planes-entrenamiento: Trae la lista de todos los planes creados

GET /api/planes-entrenamiento/{id}: Muestra el detalle de un plan en particular

POST /api/planes-entrenamiento: Crea un plan nuevo aquí es donde se valida el usuario ID contra el servicio de socios

PUT /api/planes-entrenamiento/{id}/desactivar: Desactiva un plan de entrenamiento específico

Si prefieres revisar la documentación interactiva o probar los endpoints de forma aislada, puedes ingresar directamente a las interfaces de Swagger en http://localhost:8081/swagger-ui.html para socios, o en http://localhost:8082/swagger-ui.html para entrenamientos
Pruebas Unitarias y Cobertura
Para asegurarnos de que todo funcione bien, incluimos pruebas unitarias que puedes ejecutar con el comando mvn clean test dentro de cada módulo.

Una vez que corren, Jacoco genera un reporte visual en HTML que puedes revisar abriendo el archivo target/site/jacoco/index.html en tu navegador. Cubrimos tanto la capa de controladores utilizando MockMvc como la capa de servicios como Mockito, incluyendo escenarios donde el servicio de entrenamientos debe reaccionar correctamente si el de socios no responde o el usuario no existe

Manejo de Errores y Logs
Implementamos un controlador global de excepciones "@RestControllerAdvice" en cada microservicio para que cualquier fallo devuelva una respuesta clara en formato JSON. Estructuramos los códigos de estado más comunes: 404 para recursos no encontrados, 400 para datos de entrada inválidos, 503 por si falla la comunicación entre servicios y el clásico 500 para errores internos no controlados

Además, todo el flujo está monitoreado con SLF4J "mediante la anotación de Lombok". Esto nos permite registrar en consola la creación de datos, errores de validación y los tiempos exactos que tarda el Gateway en procesar cada petición

Estructura de las Carpetas
El repositorio está organizado de la siguiente manera:

ms-socios-FitLife-SPA: Contiene el código para usuarios, membresías y autenticación

ms-entrenamientos-FitLife-SPA: Contiene la lógica para los planes de entrenamiento

api-gateway-FitLife-SPA: El enrutador principal basado en Spring Cloud Gateway

docker-compose.yml: El archivo de configuración para levantar todo en contenedores

README.md: Este archivo guía.

Integrantes del Equipo
Ignacio Canales
Jose Solis
Maria Paz Faure
Thiare Gomez