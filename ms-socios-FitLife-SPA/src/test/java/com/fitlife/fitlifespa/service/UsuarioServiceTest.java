package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.UsuarioRequestDTO;
import com.fitlife.fitlifespa.dto.UsuarioResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Usuario;
import com.fitlife.fitlifespa.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la capa Service (reglas de negocio de Usuario), aisladas
 * de Spring y de la base de datos real mediante mocks de Mockito sobre el
 * repositorio y el encoder de contraseñas. Estructura Given-When-Then.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuarioActivo() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Juan Pérez");
        u.setEmail("juan.perez@example.com");
        u.setTelefono("+56912345678");
        u.setActivo(true);
        u.setPassword("hash-encriptado");
        return u;
    }

    @Test
    @DisplayName("guardar() debería encriptar la contraseña y persistir el usuario como activo")
    void guardarDeberiaEncriptarPasswordYActivarUsuario() {
        // Given
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Juan Pérez");
        dto.setEmail("juan.perez@example.com");
        dto.setTelefono("+56912345678");
        dto.setPassword("claveOriginal123");

        when(passwordEncoder.encode("claveOriginal123")).thenReturn("hash-encriptado");
        when(repository.save(any(Usuario.class))).thenReturn(usuarioActivo());

        // When
        UsuarioResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.isActivo()).isTrue();
        assertThat(resultado.getEmail()).isEqualTo("juan.perez@example.com");
        verify(passwordEncoder, times(1)).encode("claveOriginal123");
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("desactivar() debería cambiar el estado de un usuario existente a inactivo")
    void desactivarDeberiaCambiarEstadoAInactivo() {
        // Given
        Usuario activo = usuarioActivo();
        when(repository.findById(1L)).thenReturn(Optional.of(activo));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        UsuarioResponseDTO resultado = service.desactivar(1L);

        // Then
        assertThat(resultado.isActivo()).isFalse();
        verify(repository, times(1)).save(argThat(u -> !u.isActivo()));
    }

    @Test
    @DisplayName("desactivar() debería lanzar ResourceNotFoundException si el usuario no existe")
    void desactivarDeberiaLanzarExcepcionSiNoExiste() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> service.desactivar(999L));
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("existeYActivo() debería retornar true solo si el usuario existe y está activo")
    void existeYActivoDeberiaRetornarEstadoCorrecto() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuarioActivo()));
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThat(service.existeYActivo(1L)).isTrue();
        assertThat(service.existeYActivo(2L)).isFalse();
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente todas las entidades a DTOs")
    void obtenerTodosDeberiaMapearEntidadesADto() {
        when(repository.findAll()).thenReturn(List.of(usuarioActivo()));

        List<UsuarioResponseDTO> resultado = service.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan Pérez");
    }
}
