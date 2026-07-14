package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.EntrenadorRequestDTO;
import com.fitlife.fitlifespa.dto.EntrenadorResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Entrenador;
import com.fitlife.fitlifespa.repository.EntrenadorRepository;
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
class EntrenadorServiceTest {

    @Mock
    private EntrenadorRepository repository;

    @InjectMocks
    private EntrenadorService service;

    private Entrenador entrenadorGuardado() {
        Entrenador e = new Entrenador();
        e.setId(1L);
        e.setNombre("Diego Fuentes");
        e.setEspecialidad("Crossfit");
        e.setTelefono("+56911111111");
        e.setEmail("diego.fuentes@fitlife.com");
        e.setActivo(true);
        return e;
    }

    @Test
    @DisplayName("guardar() debería crear el entrenador correctamente")
    void guardarDeberiaCrearEntrenador() {
        EntrenadorRequestDTO dto = new EntrenadorRequestDTO();
        dto.setNombre("Diego Fuentes");
        dto.setEspecialidad("Crossfit");
        dto.setTelefono("+56911111111");
        dto.setEmail("diego.fuentes@fitlife.com");

        when(repository.save(any(Entrenador.class))).thenReturn(entrenadorGuardado());

        EntrenadorResponseDTO resultado = service.guardar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.isActivo()).isTrue();
    }

    @Test
    @DisplayName("desactivar() debería cambiar el estado a inactivo")
    void desactivarDeberiaCambiarEstado() {
        Entrenador activo = entrenadorGuardado();
        when(repository.findById(1L)).thenReturn(Optional.of(activo));
        when(repository.save(any(Entrenador.class))).thenAnswer(inv -> inv.getArgument(0));

        EntrenadorResponseDTO resultado = service.desactivar(1L);

        assertThat(resultado.isActivo()).isFalse();
    }

    @Test
    @DisplayName("desactivar() debería lanzar ResourceNotFoundException si no existe")
    void desactivarDeberiaLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.desactivar(999L));
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente")
    void obtenerTodosDeberiaMapear() {
        when(repository.findAll()).thenReturn(java.util.List.of(entrenadorGuardado()));
        assertThat(service.obtenerTodos()).hasSize(1);
    }
}
