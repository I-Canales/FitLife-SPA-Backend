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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository repository;

    @InjectMocks
    private AsistenciaService service;

    private Asistencia asistenciaGuardada() {
        Asistencia a = new Asistencia();
        a.setId(1L);
        a.setUsuarioId(1L);
        a.setFecha(LocalDate.of(2026, 7, 1));
        a.setHoraEntrada("08:30");
        a.setHoraSalida(null);
        return a;
    }

    @Test
    @DisplayName("registrarEntrada() debería crear el registro sin hora de salida")
    void registrarEntradaDeberiaCrearRegistro() {
        AsistenciaRequestDTO dto = new AsistenciaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setFecha(LocalDate.of(2026, 7, 1));
        dto.setHoraEntrada("08:30");

        when(repository.save(any(Asistencia.class))).thenReturn(asistenciaGuardada());

        AsistenciaResponseDTO resultado = service.registrarEntrada(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getHoraSalida()).isNull();
    }

    @Test
    @DisplayName("registrarSalida() debería completar la hora de salida")
    void registrarSalidaDeberiaCompletarHora() {
        Asistencia sinSalida = asistenciaGuardada();
        when(repository.findById(1L)).thenReturn(Optional.of(sinSalida));
        when(repository.save(any(Asistencia.class))).thenAnswer(inv -> inv.getArgument(0));

        AsistenciaResponseDTO resultado = service.registrarSalida(1L);

        assertThat(resultado.getHoraSalida()).isNotNull();
    }

    @Test
    @DisplayName("registrarSalida() debería lanzar ResourceNotFoundException si no existe")
    void registrarSalidaDeberiaLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.registrarSalida(999L));
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente")
    void obtenerTodosDeberiaMapear() {
        when(repository.findAll()).thenReturn(java.util.List.of(asistenciaGuardada()));
        assertThat(service.obtenerTodos()).hasSize(1);
    }
}
