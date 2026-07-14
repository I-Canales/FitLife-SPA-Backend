package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.EquipoRequestDTO;
import com.fitlife.fitlifespa.dto.EquipoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Equipo;
import com.fitlife.fitlifespa.repository.EquipoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EquipoService {

    private final EquipoRepository repository;

    public EquipoService(EquipoRepository repository) {
        this.repository = repository;
    }

    public EquipoResponseDTO guardar(EquipoRequestDTO dto) {
        Equipo equipo = new Equipo();
        equipo.setNombre(dto.getNombre());
        equipo.setCategoria(dto.getCategoria());
        equipo.setFechaAdquisicion(dto.getFechaAdquisicion());
        equipo.setEstado("DISPONIBLE");

        Equipo guardado = repository.save(equipo);
        log.info("Equipo registrado correctamente id={}", guardado.getId());
        return aResponseDTO(guardado);
    }

    public Optional<EquipoResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<EquipoResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public EquipoResponseDTO marcarEnMantenimiento(Long id) {
        Equipo equipo = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de marcar en mantenimiento un equipo inexistente id={}", id);
                    return new ResourceNotFoundException("El equipo con id " + id + " no existe");
                });
        equipo.setEstado("MANTENIMIENTO");
        Equipo actualizado = repository.save(equipo);
        log.info("Equipo id={} marcado en mantenimiento", id);
        return aResponseDTO(actualizado);
    }

    private EquipoResponseDTO aResponseDTO(Equipo e) {
        return new EquipoResponseDTO(e.getId(), e.getNombre(), e.getCategoria(), e.getEstado(), e.getFechaAdquisicion());
    }
}
