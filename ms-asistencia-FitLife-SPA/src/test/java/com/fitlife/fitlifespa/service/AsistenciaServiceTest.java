package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.AsistenciaRequestDTO;
import com.fitlife.fitlifespa.dto.AsistenciaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Asistencia;
import com.fitlife.fitlifespa.repository.AsistenciaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository repository;

    @InjectMocks
    private AsistenciaService service;

    private Asistencia asistenciaGuardada() {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(1L);
        asistencia.setUsuarioId(1L);
        asistencia.setFecha(LocalDate.of(2026, 7, 1));
        asistencia.setHoraEntrada("08:30");
        asistencia.setHoraSalida(null);
        return asistencia;
    }

    @Test
    @DisplayName("registrarEntrada() debería crear el registro sin hora de salida")
    void registrarEntradaDeberiaCrearRegistro() {

        AsistenciaRequestDTO dto = new AsistenciaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setFecha(LocalDate.of(2026, 7, 1));
        dto.setHoraEntrada("08:30");

        when(repository.save(any(Asistencia.class)))
                .thenReturn(asistenciaGuardada());

        AsistenciaResponseDTO resultado = service.registrarEntrada(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNull(resultado.getHoraSalida());

        verify(repository, times(1)).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("obtenerPorId() debería devolver una asistencia")
    void obtenerPorIdExistente() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(asistenciaGuardada()));

        Optional<AsistenciaResponseDTO> resultado = service.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId() debería devolver Optional vacío")
    void obtenerPorIdInexistente() {

        when(repository.findById(100L))
                .thenReturn(Optional.empty());

        Optional<AsistenciaResponseDTO> resultado = service.obtenerPorId(100L);

        assertTrue(resultado.isEmpty());

        verify(repository).findById(100L);
    }

    @Test
    @DisplayName("obtenerTodos() debería devolver una lista")
    void obtenerTodosDeberiaMapear() {

        when(repository.findAll())
                .thenReturn(List.of(asistenciaGuardada()));

        List<AsistenciaResponseDTO> resultado = service.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());

        verify(repository).findAll();
    }

    @Test
    @DisplayName("obtenerTodos() debería devolver lista vacía")
    void obtenerTodosListaVacia() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<AsistenciaResponseDTO> resultado = service.obtenerTodos();

        assertTrue(resultado.isEmpty());

        verify(repository).findAll();
    }

    @Test
    @DisplayName("registrarSalida() debería completar la hora de salida")
    void registrarSalidaDeberiaCompletarHora() {

        Asistencia asistencia = asistenciaGuardada();

        when(repository.findById(1L))
                .thenReturn(Optional.of(asistencia));

        when(repository.save(any(Asistencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AsistenciaResponseDTO resultado = service.registrarSalida(1L);

        assertNotNull(resultado.getHoraSalida());

        verify(repository).findById(1L);
        verify(repository).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrarSalida() debería lanzar excepción")
    void registrarSalidaDeberiaLanzarExcepcion() {

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.registrarSalida(999L));

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("registrarEntrada() verifica datos correctamente")
    void registrarEntradaVerificaDatos() {

        AsistenciaRequestDTO dto = new AsistenciaRequestDTO();
        dto.setUsuarioId(15L);
        dto.setFecha(LocalDate.now());
        dto.setHoraEntrada("10:00");

        Asistencia asistencia = asistenciaGuardada();
        asistencia.setUsuarioId(15L);

        when(repository.save(any()))
                .thenReturn(asistencia);

        AsistenciaResponseDTO respuesta = service.registrarEntrada(dto);

        assertEquals(15L, respuesta.getUsuarioId());

        verify(repository).save(any());
    }

}