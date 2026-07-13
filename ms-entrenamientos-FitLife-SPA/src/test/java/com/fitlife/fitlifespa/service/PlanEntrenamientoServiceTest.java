package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.client.UsuarioClient;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoRequestDTO;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import com.fitlife.fitlifespa.model.PlanEntrenamiento;
import com.fitlife.fitlifespa.repository.PlanEntrenamientoRepository;
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

/**
 * Pruebas unitarias de PlanEntrenamientoService. El UsuarioClient (que internamente
 * usa WebClient para hablar con ms-socios) se mockea por completo: así se valida
 * la regla de negocio ("no crear un plan si el usuario remoto no es válido")
 * sin necesitar que ms-socios esté realmente levantado durante los tests.
 */
@ExtendWith(MockitoExtension.class)
class PlanEntrenamientoServiceTest {

    @Mock
    private PlanEntrenamientoRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PlanEntrenamientoService service;

    private PlanEntrenamientoRequestDTO dtoValido() {
        PlanEntrenamientoRequestDTO dto = new PlanEntrenamientoRequestDTO();
        dto.setNombrePlan("Plan Crossfit");
        dto.setEntrenador("Diego Fuentes");
        dto.setDuracionSemanas(8);
        dto.setUsuarioId(1L);
        return dto;
    }

    private PlanEntrenamiento entidadGuardada() {
        PlanEntrenamiento plan = new PlanEntrenamiento();
        plan.setId(1L);
        plan.setNombrePlan("Plan Crossfit");
        plan.setEntrenador("Diego Fuentes");
        plan.setDuracionSemanas(8);
        plan.setActivo(true);
        plan.setUsuarioId(1L);
        return plan;
    }

    @Test
    @DisplayName("guardar() debería crear el plan cuando ms-socios confirma que el usuario existe y está activo")
    void guardarDeberiaCrearPlanCuandoUsuarioEsValido() {
        // Given
        when(usuarioClient.validarUsuarioActivo(1L)).thenReturn(true);
        when(repository.save(any(PlanEntrenamiento.class))).thenReturn(entidadGuardada());

        // When
        PlanEntrenamientoResponseDTO resultado = service.guardar(dtoValido());

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.isActivo()).isTrue();
        verify(usuarioClient, times(1)).validarUsuarioActivo(1L);
        verify(repository, times(1)).save(any(PlanEntrenamiento.class));
    }

    @Test
    @DisplayName("guardar() debería lanzar ServicioRemotoException si ms-socios indica que el usuario no existe/no está activo")
    void guardarDeberiaLanzarExcepcionSiUsuarioNoEsValido() {
        // Given
        PlanEntrenamientoRequestDTO dto = dtoValido();
        dto.setUsuarioId(999L);
        when(usuarioClient.validarUsuarioActivo(999L)).thenReturn(false);

        // When / Then
        assertThrows(ServicioRemotoException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(PlanEntrenamiento.class));
    }

    @Test
    @DisplayName("guardar() debería propagar la ServicioRemotoException si ms-socios no responde (timeout/caído)")
    void guardarDeberiaPropagarExcepcionSiMsSociosNoResponde() {
        // Given
        PlanEntrenamientoRequestDTO dto = dtoValido();
        when(usuarioClient.validarUsuarioActivo(1L))
                .thenThrow(new ServicioRemotoException("No fue posible contactar a ms-socios"));

        // When / Then
        assertThrows(ServicioRemotoException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(PlanEntrenamiento.class));
    }

    @Test
    @DisplayName("desactivar() debería cambiar el estado de un plan existente a inactivo")
    void desactivarDeberiaCambiarEstadoAInactivo() {
        // Given
        PlanEntrenamiento activo = entidadGuardada();
        when(repository.findById(1L)).thenReturn(Optional.of(activo));
        when(repository.save(any(PlanEntrenamiento.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        PlanEntrenamientoResponseDTO resultado = service.desactivar(1L);

        // Then
        assertThat(resultado.isActivo()).isFalse();
        verify(repository, times(1)).save(argThat(p -> !p.isActivo()));
    }

    @Test
    @DisplayName("desactivar() debería lanzar ResourceNotFoundException si el plan no existe")
    void desactivarDeberiaLanzarExcepcionSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.desactivar(999L));
        verify(repository, never()).save(any(PlanEntrenamiento.class));
    }
}
