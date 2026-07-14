package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.EquipoRequestDTO;
import com.fitlife.fitlifespa.dto.EquipoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Equipo;
import com.fitlife.fitlifespa.repository.EquipoRepository;
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
class EquipoServiceTest {

    @Mock
    private EquipoRepository repository;

    @InjectMocks
    private EquipoService service;

    private Equipo equipoGuardado() {
        Equipo e = new Equipo();
        e.setId(1L);
        e.setNombre("Cinta de correr");
        e.setCategoria("Cardio");
        e.setEstado("DISPONIBLE");
        e.setFechaAdquisicion(LocalDate.of(2025, 1, 15));
        return e;
    }

    @Test
    @DisplayName("guardar() debería registrar el equipo como disponible")
    void guardarDeberiaCrearEquipo() {
        EquipoRequestDTO dto = new EquipoRequestDTO();
        dto.setNombre("Cinta de correr");
        dto.setCategoria("Cardio");
        dto.setFechaAdquisicion(LocalDate.of(2025, 1, 15));

        when(repository.save(any(Equipo.class))).thenReturn(equipoGuardado());

        EquipoResponseDTO resultado = service.guardar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("DISPONIBLE");
    }

    @Test
    @DisplayName("marcarEnMantenimiento() debería cambiar el estado")
    void marcarEnMantenimientoDeberiaCambiarEstado() {
        Equipo disponible = equipoGuardado();
        when(repository.findById(1L)).thenReturn(Optional.of(disponible));
        when(repository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        EquipoResponseDTO resultado = service.marcarEnMantenimiento(1L);

        assertThat(resultado.getEstado()).isEqualTo("MANTENIMIENTO");
    }

    @Test
    @DisplayName("marcarEnMantenimiento() debería lanzar ResourceNotFoundException si no existe")
    void marcarEnMantenimientoDeberiaLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.marcarEnMantenimiento(999L));
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente")
    void obtenerTodosDeberiaMapear() {
        when(repository.findAll()).thenReturn(java.util.List.of(equipoGuardado()));
        assertThat(service.obtenerTodos()).hasSize(1);
    }
}
