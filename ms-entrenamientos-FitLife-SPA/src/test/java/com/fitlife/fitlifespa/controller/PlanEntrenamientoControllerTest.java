package com.fitlife.fitlifespa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoRequestDTO;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoResponseDTO;
import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import com.fitlife.fitlifespa.service.PlanEntrenamientoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas a nivel de Controlador para PlanEntrenamientoController.
 *
 * @WebMvcTest carga solo la capa web (controller + GlobalExceptionHandler),
 * sin levantar el contexto completo, y reemplaza el PlanEntrenamientoService real por un mock.
 */
@WebMvcTest(PlanEntrenamientoController.class)
class PlanEntrenamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlanEntrenamientoService service;

    private PlanEntrenamientoResponseDTO crearPlanActivo() {
        return new PlanEntrenamientoResponseDTO(1L, "Plan Spinning", "Andrea Rojas", 8, true, 1L);
    }

    private PlanEntrenamientoResponseDTO crearPlanInactivo() {
        return new PlanEntrenamientoResponseDTO(2L, "Plan Yoga", "Felipe Muñoz", 4, false, 2L);
    }

    @Nested
    @DisplayName("GET /api/planes-entrenamiento - Obtener todos los planes")
    class ObtenerTodosTest {

        @Test
        @DisplayName("Debería retornar 200 OK con lista de planes y enlaces HATEOAS")
        void deberiaRetornar200ConLista() throws Exception {
            List<PlanEntrenamientoResponseDTO> planes = List.of(crearPlanActivo(), crearPlanInactivo());
            when(service.obtenerTodos()).thenReturn(planes);

            mockMvc.perform(get("/api/planes-entrenamiento")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.planEntrenamientoResponseDTOList").exists())
                    .andExpect(jsonPath("$._embedded.planEntrenamientoResponseDTOList.length()").value(2))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._embedded.planEntrenamientoResponseDTOList[0]._links.self").exists())
                    .andExpect(jsonPath("$._embedded.planEntrenamientoResponseDTOList[0]._links.desactivar").exists());

            verify(service, times(1)).obtenerTodos();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando no hay planes")
        void deberiaRetornar204CuandoListaVacia() throws Exception {
            when(service.obtenerTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/planes-entrenamiento")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).obtenerTodos();
        }
    }

    @Nested
    @DisplayName("GET /api/planes-entrenamiento/{id} - Obtener plan por ID")
    class ObtenerPorIdTest {

        @Test
        @DisplayName("Debería retornar 200 OK con el plan encontrado")
        void deberiaRetornar200CuandoExiste() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(crearPlanActivo()));

            mockMvc.perform(get("/api/planes-entrenamiento/1")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombrePlan").value("Plan Spinning"))
                    .andExpect(jsonPath("$.entrenador").value("Andrea Rojas"))
                    .andExpect(jsonPath("$.activo").value(true))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.desactivar").exists());

            verify(service, times(1)).obtenerPorId(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando el plan no existe")
        void deberiaRetornar404CuandoNoExiste() throws Exception {
            when(service.obtenerPorId(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/planes-entrenamiento/999")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).obtenerPorId(999L);
        }

        @Test
        @DisplayName("Debería NO incluir enlace 'desactivar' cuando el plan ya está inactivo")
        void noDeberiaIncluirLinkDesactivarCuandoYaEstaInactivo() throws Exception {
            when(service.obtenerPorId(2L)).thenReturn(Optional.of(crearPlanInactivo()));

            mockMvc.perform(get("/api/planes-entrenamiento/2")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.desactivar").doesNotExist());

            verify(service, times(1)).obtenerPorId(2L);
        }
    }

    @Nested
    @DisplayName("POST /api/planes-entrenamiento - Crear nuevo plan")
    class CrearTest {

        @Test
        @DisplayName("Debería retornar 201 Created al crear un nuevo plan")
        void deberiaRetornar201AlCrearNuevoPlan() throws Exception {
            PlanEntrenamientoRequestDTO nuevo = new PlanEntrenamientoRequestDTO();
            nuevo.setNombrePlan("Plan Crossfit");
            nuevo.setEntrenador("Diego Fuentes");
            nuevo.setDuracionSemanas(6);
            nuevo.setUsuarioId(1L);

            PlanEntrenamientoResponseDTO guardado = new PlanEntrenamientoResponseDTO(10L, "Plan Crossfit", "Diego Fuentes", 6, true, 1L);

            when(service.guardar(any(PlanEntrenamientoRequestDTO.class))).thenReturn(guardado);

            mockMvc.perform(post("/api/planes-entrenamiento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevo)))
                    .andExpect(status().isCreated());

            verify(service, times(1)).guardar(any(PlanEntrenamientoRequestDTO.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando los datos son inválidos")
        void deberiaRetornar400ConDatosInvalidos() throws Exception {
            PlanEntrenamientoRequestDTO invalido = new PlanEntrenamientoRequestDTO();
            invalido.setNombrePlan(""); // inválido: @NotBlank
            invalido.setEntrenador("");
            invalido.setDuracionSemanas(0); // inválido: @Min(1)
            invalido.setUsuarioId(null); // inválido: @NotNull

            mockMvc.perform(post("/api/planes-entrenamiento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).guardar(any(PlanEntrenamientoRequestDTO.class));
        }

        @Test
        @DisplayName("Debería retornar 503 cuando ms-socios no responde o el usuario no existe/no está activo")
        void deberiaRetornar503CuandoFallaComunicacionRemota() throws Exception {
            PlanEntrenamientoRequestDTO nuevo = new PlanEntrenamientoRequestDTO();
            nuevo.setNombrePlan("Plan Crossfit");
            nuevo.setEntrenador("Diego Fuentes");
            nuevo.setDuracionSemanas(6);
            nuevo.setUsuarioId(999L);

            when(service.guardar(any(PlanEntrenamientoRequestDTO.class)))
                    .thenThrow(new ServicioRemotoException("El usuario con id 999 no existe o no está activo en ms-socios"));

            mockMvc.perform(post("/api/planes-entrenamiento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevo)))
                    .andExpect(status().isServiceUnavailable());

            verify(service, times(1)).guardar(any(PlanEntrenamientoRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/planes-entrenamiento/{id}/desactivar - Desactivar plan")
    class DesactivarTest {

        @Test
        @DisplayName("Debería retornar 200 OK con el plan desactivado")
        void deberiaRetornar200ConPlanDesactivado() throws Exception {
            PlanEntrenamientoResponseDTO desactivado = new PlanEntrenamientoResponseDTO(1L, "Plan Spinning", "Andrea Rojas", 8, false, 1L);

            when(service.desactivar(1L)).thenReturn(desactivado);

            mockMvc.perform(put("/api/planes-entrenamiento/1/desactivar")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.activo").value(false))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.desactivar").doesNotExist());

            verify(service, times(1)).desactivar(1L);
        }
    }
}
