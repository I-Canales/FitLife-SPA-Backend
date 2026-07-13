package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.MembresiaRequestDTO;
import com.fitlife.fitlifespa.dto.MembresiaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Membresia;
import com.fitlife.fitlifespa.repository.MembresiaRepository;
import com.fitlife.fitlifespa.repository.UsuarioRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la capa Service (reglas de negocio de Membresía),
 * validando en particular la regla que impide crear una membresía para
 * un usuario inexistente.
 */
@ExtendWith(MockitoExtension.class)
class MembresiaServiceTest {

    @Mock
    private MembresiaRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MembresiaService service;

    private MembresiaRequestDTO dtoValido() {
        MembresiaRequestDTO dto = new MembresiaRequestDTO();
        dto.setTipoPlan("Premium");
        dto.setFechaInicio(LocalDate.of(2026, 7, 1));
        dto.setFechaFin(LocalDate.of(2027, 7, 1));
        dto.setPrecio(29990.0);
        dto.setUsuarioId(1L);
        return dto;
    }

    private Membresia entidadGuardada() {
        Membresia m = new Membresia();
        m.setId(1L);
        m.setTipoPlan("Premium");
        m.setFechaInicio(LocalDate.of(2026, 7, 1));
        m.setFechaFin(LocalDate.of(2027, 7, 1));
        m.setPrecio(29990.0);
        m.setUsuarioId(1L);
        return m;
    }

    @Test
    @DisplayName("guardar() debería crear la membresía cuando el usuario asociado existe")
    void guardarDeberiaCrearMembresiaCuandoUsuarioExiste() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Membresia.class))).thenReturn(entidadGuardada());

        // When
        MembresiaResponseDTO resultado = service.guardar(dtoValido());

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        verify(repository, times(1)).save(any(Membresia.class));
    }

    @Test
    @DisplayName("guardar() debería lanzar ResourceNotFoundException si el usuario no existe")
    void guardarDeberiaLanzarExcepcionSiUsuarioNoExiste() {
        // Given
        when(usuarioRepository.existsById(999L)).thenReturn(false);
        MembresiaRequestDTO dto = dtoValido();
        dto.setUsuarioId(999L);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(Membresia.class));
    }

    @Test
    @DisplayName("eliminar() debería retornar true cuando la membresía existe y se elimina")
    void eliminarDeberiaRetornarTrueCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.eliminar(1L);

        assertThat(resultado).isTrue();
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar() debería retornar false cuando la membresía no existe")
    void eliminarDeberiaRetornarFalseCuandoNoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = service.eliminar(999L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("obtenerPorId() debería retornar vacío cuando la membresía no existe")
    void obtenerPorIdDeberiaRetornarVacioSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<MembresiaResponseDTO> resultado = service.obtenerPorId(999L);

        assertThat(resultado).isEmpty();
    }
}
