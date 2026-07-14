package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.client.UsuarioClient;
import com.fitlife.fitlifespa.dto.ReservaRequestDTO;
import com.fitlife.fitlifespa.dto.ReservaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import com.fitlife.fitlifespa.model.Reserva;
import com.fitlife.fitlifespa.repository.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservaService {

    private final ReservaRepository repository;
    private final UsuarioClient usuarioClient;

    public ReservaService(ReservaRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
    }

    public ReservaResponseDTO guardar(ReservaRequestDTO dto) {
        boolean usuarioValido = usuarioClient.validarUsuarioActivo(dto.getUsuarioId());
        if (!usuarioValido) {
            log.warn("Intento de reservar para usuario inexistente/inactivo usuarioId={}", dto.getUsuarioId());
            throw new ServicioRemotoException("El usuario " + dto.getUsuarioId() + " no existe o no está activo en ms-socios");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setClaseId(dto.getClaseId());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setEstado("CONFIRMADA");

        Reserva guardada = repository.save(reserva);
        log.info("Reserva creada correctamente id={} para usuarioId={}", guardada.getId(), guardada.getUsuarioId());
        return aResponseDTO(guardada);
    }

    public Optional<ReservaResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<ReservaResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de cancelar una reserva inexistente id={}", id);
                    return new ResourceNotFoundException("La reserva con id " + id + " no existe");
                });
        reserva.setEstado("CANCELADA");
        Reserva actualizada = repository.save(reserva);
        log.info("Reserva id={} cancelada correctamente", id);
        return aResponseDTO(actualizada);
    }

    private ReservaResponseDTO aResponseDTO(Reserva r) {
        return new ReservaResponseDTO(r.getId(), r.getUsuarioId(), r.getClaseId(), r.getFechaReserva(), r.getEstado());
    }
}
