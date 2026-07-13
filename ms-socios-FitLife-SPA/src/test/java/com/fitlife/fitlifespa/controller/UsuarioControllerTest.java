package com.fitlife.fitlifespa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.fitlifespa.dto.UsuarioRequestDTO;
import com.fitlife.fitlifespa.dto.UsuarioResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.UsuarioService;
import com.fitlife.fitlifespa.config.SecurityConfig;
import com.fitlife.fitlifespa.config.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
 * Pruebas a nivel de Controlador para UsuarioController.
 *
 * @WebMvcTest carga solo la capa web (controller + GlobalExceptionHandler),
 * sin levantar el contexto completo, y reemplaza el UsuarioService real por un mock.
 */
@WebMvcTest(controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService service;

    private UsuarioResponseDTO crearUsuarioActivo() {
        return new UsuarioResponseDTO(1L, "Juan Pérez", "juan.perez@example.com", "+56912345678", true);
    }

    private UsuarioResponseDTO crearUsuarioInactivo() {
        return new UsuarioResponseDTO(2L, "María López", "maria.lopez@example.com", "+56987654321", false);
    }

    @Nested
    @DisplayName("GET /api/usuarios - Obtener todos los usuarios")
    class ObtenerTodosTest {

        @Test
        @DisplayName("Debería retornar 200 OK con lista de usuarios y enlaces HATEOAS")
        void deberiaRetornar200ConLista() throws Exception {
            List<UsuarioResponseDTO> usuarios = List.of(crearUsuarioActivo(), crearUsuarioInactivo());
            when(service.obtenerTodos()).thenReturn(usuarios);

            mockMvc.perform(get("/api/usuarios")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.usuarioResponseDTOList").exists())
                    .andExpect(jsonPath("$._embedded.usuarioResponseDTOList.length()").value(2))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._embedded.usuarioResponseDTOList[0]._links.self").exists())
                    .andExpect(jsonPath("$._embedded.usuarioResponseDTOList[0]._links.desactivar").exists());

            verify(service, times(1)).obtenerTodos();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando no hay usuarios")
        void deberiaRetornar204CuandoListaVacia() throws Exception {
            when(service.obtenerTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/usuarios")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).obtenerTodos();
        }
    }

    @Nested
    @DisplayName("GET /api/usuarios/{id} - Obtener usuario por ID")
    class ObtenerPorIdTest {

        @Test
        @DisplayName("Debería retornar 200 OK con el usuario encontrado")
        void deberiaRetornar200CuandoExiste() throws Exception {
            UsuarioResponseDTO usuario = crearUsuarioActivo();
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(usuario));

            mockMvc.perform(get("/api/usuarios/1")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                    .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                    .andExpect(jsonPath("$.activo").value(true))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.desactivar").exists());

            verify(service, times(1)).obtenerPorId(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando el usuario no existe")
        void deberiaRetornar404CuandoNoExiste() throws Exception {
            when(service.obtenerPorId(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/usuarios/999")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));

            verify(service, times(1)).obtenerPorId(999L);
        }

        @Test
        @DisplayName("Debería NO incluir enlace 'desactivar' cuando el usuario ya está inactivo")
        void noDeberiaIncluirLinkDesactivarCuandoYaEstaInactivo() throws Exception {
            UsuarioResponseDTO inactivo = crearUsuarioInactivo();
            when(service.obtenerPorId(2L)).thenReturn(Optional.of(inactivo));

            mockMvc.perform(get("/api/usuarios/2")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.desactivar").doesNotExist());

            verify(service, times(1)).obtenerPorId(2L);
        }
    }

    @Nested
    @DisplayName("POST /api/usuarios - Crear nuevo usuario")
    class CrearTest {

        @Test
        @DisplayName("Debería retornar 201 Created al crear un nuevo usuario")
        void deberiaRetornar201AlCrearNuevoUsuario() throws Exception {
            UsuarioRequestDTO nuevo = new UsuarioRequestDTO();
            nuevo.setNombre("Carlos Soto");
            nuevo.setEmail("carlos.soto@example.com");
            nuevo.setTelefono("+56911112222");
            nuevo.setPassword("password123");

            UsuarioResponseDTO guardado = new UsuarioResponseDTO(10L, "Carlos Soto", "carlos.soto@example.com", "+56911112222", true);

            when(service.guardar(any(UsuarioRequestDTO.class))).thenReturn(guardado);

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevo)))
                    .andExpect(status().isCreated());

            verify(service, times(1)).guardar(any(UsuarioRequestDTO.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando los datos son inválidos")
        void deberiaRetornar400ConDatosInvalidos() throws Exception {
            UsuarioRequestDTO invalido = new UsuarioRequestDTO();
            invalido.setNombre(""); // inválido: @NotBlank
            invalido.setEmail("no-es-un-email"); // inválido: @Email
            invalido.setTelefono("");
            invalido.setPassword("123"); // inválido: menos de 6 caracteres

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores").exists());

            verify(service, never()).guardar(any(UsuarioRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/usuarios/{id}/desactivar - Desactivar usuario")
    class DesactivarTest {

        @Test
        @DisplayName("Debería retornar 200 OK con el usuario desactivado")
        void deberiaRetornar200ConUsuarioDesactivado() throws Exception {
            UsuarioResponseDTO desactivado = new UsuarioResponseDTO(1L, "Juan Pérez", "juan.perez@example.com", "+56912345678", false);

            when(service.desactivar(1L)).thenReturn(desactivado);

            mockMvc.perform(put("/api/usuarios/1/desactivar")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.activo").value(false))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.desactivar").doesNotExist());

            verify(service, times(1)).desactivar(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando el usuario a desactivar no existe")
        void deberiaRetornar404CuandoNoExisteAlDesactivar() throws Exception {
            when(service.desactivar(999L)).thenThrow(new ResourceNotFoundException("El usuario con id 999 no existe"));

            mockMvc.perform(put("/api/usuarios/999/desactivar")
                            .accept(MediaTypes.HAL_JSON_VALUE))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).desactivar(999L);
        }
    }
}
