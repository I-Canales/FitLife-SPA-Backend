package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.RegistroProgresoRequestDTO;
import com.fitlife.fitlifespa.dto.RegistroProgresoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.RegistroProgreso;
import com.fitlife.fitlifespa.repository.RegistroProgresoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegistroProgresoService {

    private final RegistroProgresoRepository repository;

    public RegistroProgresoService(RegistroProgresoRepository repository) {
        this.repository = repository;
    }

    public RegistroProgresoResponseDTO guardar(RegistroProgresoRequestDTO dto) {
        RegistroProgreso r = new RegistroProgreso();
        r.setUsuarioId(dto.getUsuarioId());
        r.setFecha(dto.getFecha());
        r.setPeso(dto.getPeso());
        r.setGrasaCorporal(dto.getGrasaCorporal());
        r.setObservaciones(dto.getObservaciones());

        RegistroProgreso guardado = repository.save(r);
        log.info("Registro de progreso creado correctamente id={} para usuarioId={}", guardado.getId(), guardado.getUsuarioId());
        return aResponseDTO(guardado);
    }

    public Optional<RegistroProgresoResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<RegistroProgresoResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar un registro de progreso inexistente id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Registro de progreso id={} eliminado correctamente", id);
        return true;
    }

    private RegistroProgresoResponseDTO aResponseDTO(RegistroProgreso r) {
        return new RegistroProgresoResponseDTO(r.getId(), r.getUsuarioId(), r.getFecha(), r.getPeso(), r.getGrasaCorporal(), r.getObservaciones());
    }
}
