package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.ClaseGrupalRequestDTO;
import com.fitlife.fitlifespa.dto.ClaseGrupalResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.ClaseGrupal;
import com.fitlife.fitlifespa.repository.ClaseGrupalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaseGrupalServiceTest {

    @Mock
    private ClaseGrupalRepository repository;

    @InjectMocks
    private ClaseGrupalService service;

    private ClaseGrupal claseGuardada() {
        ClaseGrupal c = new ClaseGrupal();
        c.setId(1L);
        c.setNombre("Zumba");
        c.setTipo("Cardio");
        c.setHorario("Lunes 18:00");
        c.setCupoMaximo(20);
        c.setEntrenadorId(1L);
        c.setActiva(true);
        return c;
    }

    @Test
    @DisplayName("guardar() debería crear la clase correctamente")
    void guardarDeberiaCrearClase() {
        ClaseGrupalRequestDTO dto = new ClaseGrupalRequestDTO();
        dto.setNombre("Zumba");
        dto.setTipo("Cardio");
        dto.setHorario("Lunes 18:00");
        dto.setCupoMaximo(20);
        dto.setEntrenadorId(1L);

        when(repository.save(any(ClaseGrupal.class))).thenReturn(claseGuardada());

        ClaseGrupalResponseDTO resultado = service.guardar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.isActiva()).isTrue();
        verify(repository, times(1)).save(any(ClaseGrupal.class));
    }

    @Test
    @DisplayName("desactivar() debería cambiar el estado a inactiva")
    void desactivarDeberiaCambiarEstado() {
        ClaseGrupal activa = claseGuardada();
        when(repository.findById(1L)).thenReturn(Optional.of(activa));
        when(repository.save(any(ClaseGrupal.class))).thenAnswer(inv -> inv.getArgument(0));

        ClaseGrupalResponseDTO resultado = service.desactivar(1L);

        assertThat(resultado.isActiva()).isFalse();
    }

    @Test
    @DisplayName("desactivar() debería lanzar ResourceNotFoundException si no existe")
    void desactivarDeberiaLanzarExcepcionSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.desactivar(999L));
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente")
    void obtenerTodosDeberiaMapear() {
        when(repository.findAll()).thenReturn(java.util.List.of(claseGuardada()));
        assertThat(service.obtenerTodos()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar vacío si no existe")
    void obtenerPorIdVacioSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThat(service.obtenerPorId(999L)).isEmpty();
    }
}
