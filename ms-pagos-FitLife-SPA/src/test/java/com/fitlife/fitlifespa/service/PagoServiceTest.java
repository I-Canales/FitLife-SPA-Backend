package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.PagoRequestDTO;
import com.fitlife.fitlifespa.dto.PagoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Pago;
import com.fitlife.fitlifespa.repository.PagoRepository;
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
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @InjectMocks
    private PagoService service;

    private Pago pagoGuardado() {
        Pago p = new Pago();
        p.setId(1L);
        p.setUsuarioId(1L);
        p.setMonto(29990.0);
        p.setFechaPago(LocalDate.of(2026, 7, 1));
        p.setMetodoPago("Tarjeta de crédito");
        p.setEstado("PAGADO");
        return p;
    }

    @Test
    @DisplayName("guardar() debería crear el pago como PAGADO")
    void guardarDeberiaCrearPago() {
        PagoRequestDTO dto = new PagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMonto(29990.0);
        dto.setFechaPago(LocalDate.of(2026, 7, 1));
        dto.setMetodoPago("Tarjeta de crédito");

        when(repository.save(any(Pago.class))).thenReturn(pagoGuardado());

        PagoResponseDTO resultado = service.guardar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("PAGADO");
    }

    @Test
    @DisplayName("anular() debería cambiar el estado a ANULADO")
    void anularDeberiaCambiarEstado() {
        Pago pagado = pagoGuardado();
        when(repository.findById(1L)).thenReturn(Optional.of(pagado));
        when(repository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponseDTO resultado = service.anular(1L);

        assertThat(resultado.getEstado()).isEqualTo("ANULADO");
    }

    @Test
    @DisplayName("anular() debería lanzar ResourceNotFoundException si no existe")
    void anularDeberiaLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.anular(999L));
    }

    @Test
    @DisplayName("obtenerTodos() debería mapear correctamente")
    void obtenerTodosDeberiaMapear() {
        when(repository.findAll()).thenReturn(java.util.List.of(pagoGuardado()));
        assertThat(service.obtenerTodos()).hasSize(1);
    }
}
