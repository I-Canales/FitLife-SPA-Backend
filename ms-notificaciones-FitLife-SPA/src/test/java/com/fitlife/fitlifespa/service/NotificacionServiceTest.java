package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.NotificacionRequestDTO;
import com.fitlife.fitlifespa.dto.NotificacionResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Notificacion;
import com.fitlife.fitlifespa.repository.NotificacionRepository;
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
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository;

    @InjectMocks
    private NotificacionService service;

    private Notificacion notificacionGuardada() {
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setUsuarioId(1L);
        n.setMensaje("Tu membresía vence en 3 días");
        n.setTipo("RECORDATORIO");
        n.setLeida(false);
        n.setFechaEnvio(LocalDate.of(2026, 7, 1));
        return n;
    }

    @Test
    @DisplayName("guardar() debería crear la notificación como no leída")
    void guardarDeberiaCrearNotificacion() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMensaje("Tu membresía vence en 3 días");
        dto.setTipo("RECORDATORIO");
        dto.setFechaEnvio(LocalDate.of(2026, 7, 1));

        when(repository.save(any(Notificacion.class))).thenReturn(notificacionGuardada());

        NotificacionResponseDTO resultado = service.guardar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.isLeida()).isFalse();
    }

    @Test
    @DisplayName("marcarComoLeida() debería cambiar el estado a leída")
    void marcarComoLeidaDeberiaCambiarEstado() {
        Notificacion noLeida = notificacionGuardada();
        when(repository.findById(1L)).thenReturn(Optional.of(noLeida));
        when(repository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificacionResponseDTO resultado = service.marcarComoLeida(1L);

        assertThat(resultado.isLeida()).isTrue();
    }

    @Test
    @DisplayName("marcarComoLeida() debería lanzar ResourceNotFoundException si no existe")
    void marcarComoLeidaDeberiaLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.marcarComoLeida(999L));
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente")
    void obtenerTodosDeberiaMapear() {
        when(repository.findAll()).thenReturn(java.util.List.of(notificacionGuardada()));
        assertThat(service.obtenerTodos()).hasSize(1);
    }
}
