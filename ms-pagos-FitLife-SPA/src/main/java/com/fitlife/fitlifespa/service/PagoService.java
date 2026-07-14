package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.PagoRequestDTO;
import com.fitlife.fitlifespa.dto.PagoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Pago;
import com.fitlife.fitlifespa.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PagoService {

    private final PagoRepository repository;

    public PagoService(PagoRepository repository) {
        this.repository = repository;
    }

    public PagoResponseDTO guardar(PagoRequestDTO dto) {
        Pago pago = new Pago();
        pago.setUsuarioId(dto.getUsuarioId());
        pago.setMonto(dto.getMonto());
        pago.setFechaPago(dto.getFechaPago());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado("PAGADO");

        Pago guardado = repository.save(pago);
        log.info("Pago registrado correctamente id={} para usuarioId={}", guardado.getId(), guardado.getUsuarioId());
        return aResponseDTO(guardado);
    }

    public Optional<PagoResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<PagoResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public PagoResponseDTO anular(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de anular un pago inexistente id={}", id);
                    return new ResourceNotFoundException("El pago con id " + id + " no existe");
                });
        pago.setEstado("ANULADO");
        Pago actualizado = repository.save(pago);
        log.info("Pago id={} anulado correctamente", id);
        return aResponseDTO(actualizado);
    }

    private PagoResponseDTO aResponseDTO(Pago p) {
        return new PagoResponseDTO(p.getId(), p.getUsuarioId(), p.getMonto(), p.getFechaPago(), p.getMetodoPago(), p.getEstado());
    }
}
