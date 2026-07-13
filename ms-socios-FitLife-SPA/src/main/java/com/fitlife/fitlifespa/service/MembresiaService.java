package com.fitlife.fitlifespa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fitlife.fitlifespa.dto.MembresiaRequestDTO;
import com.fitlife.fitlifespa.dto.MembresiaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Membresia;
import com.fitlife.fitlifespa.repository.MembresiaRepository;
import com.fitlife.fitlifespa.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MembresiaService {

    private final MembresiaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public MembresiaService(MembresiaRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public MembresiaResponseDTO guardar(MembresiaRequestDTO dto) {
        // Regla de negocio: no se puede crear una membresía para un usuario que no existe.
        if (!usuarioRepository.existsById(dto.getUsuarioId())) {
            log.warn("Intento de crear membresía para usuario inexistente id={}", dto.getUsuarioId());
            throw new ResourceNotFoundException("El usuario con id " + dto.getUsuarioId() + " no existe");
        }

        Membresia membresia = new Membresia();
        membresia.setTipoPlan(dto.getTipoPlan());
        membresia.setFechaInicio(dto.getFechaInicio());
        membresia.setFechaFin(dto.getFechaFin());
        membresia.setPrecio(dto.getPrecio());
        membresia.setUsuarioId(dto.getUsuarioId());

        Membresia guardada = repository.save(membresia);
        log.info("Membresía creada correctamente con id={} para usuarioId={}", guardada.getId(), guardada.getUsuarioId());
        return aResponseDTO(guardada);
    }

    public Optional<MembresiaResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<MembresiaResponseDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar una membresía inexistente id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Membresía id={} eliminada correctamente", id);
        return true;
    }

    private MembresiaResponseDTO aResponseDTO(Membresia membresia) {
        return new MembresiaResponseDTO(
                membresia.getId(),
                membresia.getTipoPlan(),
                membresia.getFechaInicio(),
                membresia.getFechaFin(),
                membresia.getPrecio(),
                membresia.getUsuarioId()
        );
    }
}
