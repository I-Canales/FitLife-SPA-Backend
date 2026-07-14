package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.RegistroProgresoRequestDTO;
import com.fitlife.fitlifespa.dto.RegistroProgresoResponseDTO;
import com.fitlife.fitlifespa.model.RegistroProgreso;
import com.fitlife.fitlifespa.repository.RegistroProgresoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroProgresoServiceTest {

    @Mock
    private RegistroProgresoRepository repository;

    @InjectMocks
    private RegistroProgresoService service;

    private RegistroProgreso registroGuardado() {
        RegistroProgreso r = new RegistroProgreso();
        r.setId(1L);
        r.setUsuarioId(1L);
        r.setFecha(LocalDate.of(2026, 7, 1));
        r.setPeso(79.5);
        r.setGrasaCorporal(20.1);
        r.setObservaciones("Buen progreso este mes");
        return r;
    }

    @Test
    @DisplayName("guardar() debería crear el registro correctamente")
    void guardarDeberiaCrearRegistro() {
        RegistroProgresoRequestDTO dto = new RegistroProgresoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setFecha(LocalDate.of(2026, 7, 1));
        dto.setPeso(79.5);
        dto.setGrasaCorporal(20.1);
        dto.setObservaciones("Buen progreso este mes");

        when(repository.save(any(RegistroProgreso.class))).thenReturn(registroGuardado());

        RegistroProgresoResponseDTO resultado = service.guardar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPeso()).isEqualTo(79.5);
    }

    @Test
    @DisplayName("eliminar() debería retornar true cuando el registro existe")
    void eliminarDeberiaRetornarTrueCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.eliminar(1L);

        assertThat(resultado).isTrue();
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar() debería retornar false cuando no existe")
    void eliminarDeberiaRetornarFalseCuandoNoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = service.eliminar(999L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar vacío si no existe")
    void obtenerPorIdDeberiaRetornarVacio() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThat(service.obtenerPorId(999L)).isEmpty();
    }
}
