package com.fitlife.fitlifespa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitlife.fitlifespa.dto.UsuarioRequestDTO;
import com.fitlife.fitlifespa.dto.UsuarioResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Usuario;
import com.fitlife.fitlifespa.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setActivo(true);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        Usuario guardado = repository.save(usuario);
        log.info("Usuario creado correctamente con id={}", guardado.getId());
        return aResponseDTO(guardado);
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO desactivar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de desactivar un usuario inexistente id={}", id);
                    return new ResourceNotFoundException("El usuario con id " + id + " no existe");
                });
        usuario.setActivo(false);
        Usuario actualizado = repository.save(usuario);
        log.info("Usuario id={} desactivado correctamente", id);
        return aResponseDTO(actualizado);
    }

    /**
     * Usado por otros microservicios (vía Gateway) para validar si un usuario
     * existe y está activo antes de crear recursos asociados (ej. planes de entrenamiento).
     */
    public boolean existeYActivo(Long id) {
        return repository.findById(id).map(Usuario::isActivo).orElse(false);
    }

    private UsuarioResponseDTO aResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.isActivo()
        );
    }
}
