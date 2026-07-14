package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.EntrenadorRequestDTO;
import com.fitlife.fitlifespa.dto.EntrenadorResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Entrenador;
import com.fitlife.fitlifespa.repository.EntrenadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EntrenadorService {

    private final EntrenadorRepository repository;

    public EntrenadorService(EntrenadorRepository repository) {
        this.repository = repository;
    }

    public EntrenadorResponseDTO guardar(EntrenadorRequestDTO dto) {
        Entrenador entrenador = new Entrenador();
        entrenador.setNombre(dto.getNombre());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setTelefono(dto.getTelefono());
        entrenador.setEmail(dto.getEmail());
        entrenador.setActivo(true);

        Entrenador guardado = repository.save(entrenador);
        log.info("Entrenador creado correctamente id={}", guardado.getId());
        return aResponseDTO(guardado);
    }

    public Optional<EntrenadorResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<EntrenadorResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public EntrenadorResponseDTO desactivar(Long id) {
        Entrenador entrenador = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de desactivar un entrenador inexistente id={}", id);
                    return new ResourceNotFoundException("El entrenador con id " + id + " no existe");
                });
        entrenador.setActivo(false);
        Entrenador actualizado = repository.save(entrenador);
        log.info("Entrenador id={} desactivado correctamente", id);
        return aResponseDTO(actualizado);
    }

    private EntrenadorResponseDTO aResponseDTO(Entrenador e) {
        return new EntrenadorResponseDTO(e.getId(), e.getNombre(), e.getEspecialidad(), e.getTelefono(), e.getEmail(), e.isActivo());
    }
}
