package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.ClaseGrupalRequestDTO;
import com.fitlife.fitlifespa.dto.ClaseGrupalResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.ClaseGrupal;
import com.fitlife.fitlifespa.repository.ClaseGrupalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClaseGrupalService {

    private final ClaseGrupalRepository repository;

    public ClaseGrupalService(ClaseGrupalRepository repository) {
        this.repository = repository;
    }

    public ClaseGrupalResponseDTO guardar(ClaseGrupalRequestDTO dto) {
        ClaseGrupal clase = new ClaseGrupal();
        clase.setNombre(dto.getNombre());
        clase.setTipo(dto.getTipo());
        clase.setHorario(dto.getHorario());
        clase.setCupoMaximo(dto.getCupoMaximo());
        clase.setEntrenadorId(dto.getEntrenadorId());
        clase.setActiva(true);

        ClaseGrupal guardada = repository.save(clase);
        log.info("Clase grupal creada correctamente id={}", guardada.getId());
        return aResponseDTO(guardada);
    }

    public Optional<ClaseGrupalResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<ClaseGrupalResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public ClaseGrupalResponseDTO desactivar(Long id) {
        ClaseGrupal clase = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de desactivar una clase inexistente id={}", id);
                    return new ResourceNotFoundException("La clase con id " + id + " no existe");
                });
        clase.setActiva(false);
        ClaseGrupal actualizada = repository.save(clase);
        log.info("Clase id={} desactivada correctamente", id);
        return aResponseDTO(actualizada);
    }

    private ClaseGrupalResponseDTO aResponseDTO(ClaseGrupal c) {
        return new ClaseGrupalResponseDTO(c.getId(), c.getNombre(), c.getTipo(), c.getHorario(),
                c.getCupoMaximo(), c.getEntrenadorId(), c.isActiva());
    }
}
