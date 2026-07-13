package com.fitlife.fitlifespa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.fitlifespa.dto.MembresiaRequestDTO;
import com.fitlife.fitlifespa.dto.MembresiaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.MembresiaService;
import com.fitlife.fitlifespa.config.SecurityConfig;
import com.fitlife.fitlifespa.config.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas a nivel de Controlador para MembresiaController.
 *
 * @WebMvcTest carga solo la capa web (controller + GlobalExceptionHandler),
 * sin levantar el contexto completo, y reemplaza el MembresiaService real por un mock.
 *
 * @AutoConfigureMockMvc(addFilters = false) desactiva el filtro de Spring Security
 * durante estos tests, ya que aquí se prueba la lógica del controlador, no la
 * autenticación (que se prueba a nivel de integración con /api/auth/login).
 */
@WebMvcTest(controllers = MembresiaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
class MembresiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MembresiaService service;

    private MembresiaResponseDTO crearMembresia() {
        return new MembresiaResponseDTO(1L, "Premium",
                LocalDate.of(2026, 7, 1), LocalDate.of(2027, 7, 1), 29990.0, 1L);
    }

    @Nested
    @DisplayName("GET /api/membresias - Obtener todas las membresías")
    class ObtenerTodasTest {

        @Test
        @DisplayName("Debería retornar 200 OK con lista de membresías y enlaces HATEOAS")
        void deberiaRetornar200ConLista() throws Exception {
            List<MembresiaResponseDTO> membresias = List.of(crearMembresia());
            when(service.obtenerTodos()).thenReturn(membresias);

            mockMvc.perform(get("/api/membresias")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.membresiaResponseDTOList").exists())
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._embedded.membresiaResponseDTOList[0]._links.self").exists());

            verify(service, times(1)).obtenerTodos();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando no hay membresías")
        void deberiaRetornar204CuandoListaVacia() throws Exception {
            when(service.obtenerTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/membresias")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).obtenerTodos();
        }
    }

    @Nested
    @DisplayName("GET /api/membresias/{id} - Obtener membresía por ID")
    class ObtenerPorIdTest {

        @Test
        @DisplayName("Debería retornar 200 OK con la membresía encontrada")
        void deberiaRetornar200CuandoExiste() throws Exception {
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(crearMembresia()));

            mockMvc.perform(get("/api/membresias/1")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.tipoPlan").value("Premium"))
                    .andExpect(jsonPath("$._links.self").exists());

            verify(service, times(1)).obtenerPorId(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando la membresía no existe")
        void deberiaRetornar404CuandoNoExiste() throws Exception {
            when(service.obtenerPorId(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/membresias/999")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).obtenerPorId(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/membresias - Crear nueva membresía")
    class CrearTest {

        @Test
        @DisplayName("Debería retornar 201 Created al crear una nueva membresía")
        void deberiaRetornar201AlCrear() throws Exception {
            MembresiaRequestDTO nueva = new MembresiaRequestDTO();
            nueva.setTipoPlan("Premium");
            nueva.setFechaInicio(LocalDate.of(2026, 7, 1));
            nueva.setFechaFin(LocalDate.of(2027, 7, 1));
            nueva.setPrecio(29990.0);
            nueva.setUsuarioId(1L);

            MembresiaResponseDTO guardada = crearMembresia();

            when(service.guardar(any(MembresiaRequestDTO.class))).thenReturn(guardada);

            mockMvc.perform(post("/api/membresias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nueva)))
                    .andExpect(status().isCreated());

            verify(service, times(1)).guardar(any(MembresiaRequestDTO.class));
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando el usuario asociado no existe")
        void deberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
            MembresiaRequestDTO nueva = new MembresiaRequestDTO();
            nueva.setTipoPlan("Premium");
            nueva.setFechaInicio(LocalDate.of(2026, 7, 1));
            nueva.setFechaFin(LocalDate.of(2027, 7, 1));
            nueva.setPrecio(29990.0);
            nueva.setUsuarioId(999L);

            when(service.guardar(any(MembresiaRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("El usuario con id 999 no existe"));

            mockMvc.perform(post("/api/membresias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nueva)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).guardar(any(MembresiaRequestDTO.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando el precio es inválido")
        void deberiaRetornar400ConPrecioInvalido() throws Exception {
            MembresiaRequestDTO invalida = new MembresiaRequestDTO();
            invalida.setTipoPlan("Premium");
            invalida.setFechaInicio(LocalDate.of(2026, 7, 1));
            invalida.setFechaFin(LocalDate.of(2027, 7, 1));
            invalida.setPrecio(-10.0); // inválido: @Positive
            invalida.setUsuarioId(1L);

            mockMvc.perform(post("/api/membresias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalida)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).guardar(any(MembresiaRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/membresias/{id} - Eliminar membresía")
    class EliminarTest {

        @Test
        @DisplayName("Debería retornar 204 No Content al eliminar correctamente")
        void deberiaRetornar204AlEliminar() throws Exception {
            when(service.eliminar(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/membresias/1"))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).eliminar(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found al eliminar una membresía inexistente")
        void deberiaRetornar404AlEliminarInexistente() throws Exception {
            when(service.eliminar(999L)).thenReturn(false);

            mockMvc.perform(delete("/api/membresias/999"))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).eliminar(999L);
        }
    }
}
