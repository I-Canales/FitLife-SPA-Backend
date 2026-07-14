package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.client.UsuarioClient;
import com.fitlife.fitlifespa.dto.ReservaRequestDTO;
import com.fitlife.fitlifespa.dto.ReservaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import com.fitlife.fitlifespa.model.Reserva;
import com.fitlife.fitlifespa.repository.ReservaRepository;
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

/**
 * Igual que en ms-entrenamientos, el UsuarioClient (WebClient hacia ms-socios)
 * se mockea por completo para validar la regla de negocio sin depender de que
 * ms-socios esté realmente levantado durante los tests.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ReservaService service;

    private ReservaRequestDTO dtoValido() {
        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setClaseId(1L);
        dto.setFechaReserva(LocalDate.of(2026, 7, 5));
        return dto;
    }

    private Reserva reservaGuardada() {
        Reserva r = new Reserva();
        r.setId(1L);
        r.setUsuarioId(1L);
        r.setClaseId(1L);
        r.setFechaReserva(LocalDate.of(2026, 7, 5));
        r.setEstado("CONFIRMADA");
        return r;
    }

    @Test
    @DisplayName("guardar() debería crear la reserva cuando ms-socios confirma que el usuario existe y está activo")
    void guardarDeberiaCrearReservaCuandoUsuarioEsValido() {
        when(usuarioClient.validarUsuarioActivo(1L)).thenReturn(true);
        when(repository.save(any(Reserva.class))).thenReturn(reservaGuardada());

        ReservaResponseDTO resultado = service.guardar(dtoValido());

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("CONFIRMADA");
        verify(usuarioClient, times(1)).validarUsuarioActivo(1L);
    }

    @Test
    @DisplayName("guardar() debería lanzar ServicioRemotoException si ms-socios indica que el usuario no es válido")
    void guardarDeberiaLanzarExcepcionSiUsuarioNoEsValido() {
        ReservaRequestDTO dto = dtoValido();
        dto.setUsuarioId(999L);
        when(usuarioClient.validarUsuarioActivo(999L)).thenReturn(false);

        assertThrows(ServicioRemotoException.class, () -> service.guardar(dto));
        verify(repository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("guardar() debería propagar la excepción si ms-socios no responde")
    void guardarDeberiaPropagarExcepcionSiMsSociosNoResponde() {
        when(usuarioClient.validarUsuarioActivo(1L))
                .thenThrow(new ServicioRemotoException("No fue posible contactar a ms-socios"));

        assertThrows(ServicioRemotoException.class, () -> service.guardar(dtoValido()));
        verify(repository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("cancelar() debería cambiar el estado a CANCELADA")
    void cancelarDeberiaCambiarEstado() {
        Reserva confirmada = reservaGuardada();
        when(repository.findById(1L)).thenReturn(Optional.of(confirmada));
        when(repository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponseDTO resultado = service.cancelar(1L);

        assertThat(resultado.getEstado()).isEqualTo("CANCELADA");
    }

    @Test
    @DisplayName("cancelar() debería lanzar ResourceNotFoundException si la reserva no existe")
    void cancelarDeberiaLanzarExcepcionSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.cancelar(999L));
    }
}
