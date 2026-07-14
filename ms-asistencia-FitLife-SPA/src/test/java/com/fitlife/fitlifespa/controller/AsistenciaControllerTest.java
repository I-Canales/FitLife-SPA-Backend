package com.fitlife.fitlifespa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.fitlifespa.dto.AsistenciaRequestDTO;
import com.fitlife.fitlifespa.dto.AsistenciaResponseDTO;
import com.fitlife.fitlifespa.service.AsistenciaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@WebMvcTest(AsistenciaController.class)
class AsistenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private AsistenciaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private AsistenciaResponseDTO crearRespuesta() {

        AsistenciaResponseDTO dto = new AsistenciaResponseDTO();

        dto.setId(1L);
        dto.setUsuarioId(1L);
        dto.setFecha(LocalDate.of(2026,7,1));
        dto.setHoraEntrada("08:30");
        dto.setHoraSalida(null);

        return dto;
    }

    @Test
    @DisplayName("GET /api/asistencia")
    void obtenerTodos() throws Exception {

        when(service.obtenerTodos())
                .thenReturn(List.of(crearRespuesta()));

        mockMvc.perform(get("/api/asistencia")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/asistencia sin datos")
    void obtenerTodosVacio() throws Exception {

        when(service.obtenerTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/asistencia"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET por id")
    void obtenerPorId() throws Exception {

        when(service.obtenerPorId(1L))
                .thenReturn(Optional.of(crearRespuesta()));

        mockMvc.perform(get("/api/asistencia/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST registrar")
    void registrarEntrada() throws Exception {

        AsistenciaRequestDTO dto = new AsistenciaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setFecha(LocalDate.of(2026,7,1));
        dto.setHoraEntrada("08:30");

        when(service.registrarEntrada(any()))
                .thenReturn(crearRespuesta());

        mockMvc.perform(post("/api/asistencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT registrar salida")
    void registrarSalida() throws Exception {

        AsistenciaResponseDTO dto = crearRespuesta();
        dto.setHoraSalida("10:30");

        when(service.registrarSalida(1L))
                .thenReturn(dto);

        mockMvc.perform(put("/api/asistencia/1/salida"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET inexistente")
    void obtenerPorIdNoExiste() throws Exception {

        when(service.obtenerPorId(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/asistencia/999"))
                .andExpect(status().is4xxClientError());
    }

}