package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.client.UsuarioClient;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoRequestDTO;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import com.fitlife.fitlifespa.model.PlanEntrenamiento;
import com.fitlife.fitlifespa.repository.PlanEntrenamientoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlanEntrenamientoService {

    private final PlanEntrenamientoRepository repository;
    private final UsuarioClient usuarioClient;

    public PlanEntrenamientoService(PlanEntrenamientoRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
    }

    public PlanEntrenamientoResponseDTO guardar(PlanEntrenamientoRequestDTO dto) {
        // Comunicación entre microservicios: antes de crear el plan, se valida
        // contra ms-socios que el usuario dueño exista y esté activo.
        boolean usuarioValido = usuarioClient.validarUsuarioActivo(dto.getUsuarioId());
        if (!usuarioValido) {
            log.warn("Intento de crear plan de entrenamiento para usuario inexistente/inactivo usuarioId={}", dto.getUsuarioId());
            throw new ServicioRemotoException(
                    "El usuario con id " + dto.getUsuarioId() + " no existe o no está activo en ms-socios");
        }

        PlanEntrenamiento plan = new PlanEntrenamiento();
        plan.setNombrePlan(dto.getNombrePlan());
        plan.setEntrenador(dto.getEntrenador());
        plan.setDuracionSemanas(dto.getDuracionSemanas());
        plan.setUsuarioId(dto.getUsuarioId());
        plan.setActivo(true);

        PlanEntrenamiento guardado = repository.save(plan);
        log.info("Plan de entrenamiento creado correctamente id={} para usuarioId={}", guardado.getId(), guardado.getUsuarioId());
        return aResponseDTO(guardado);
    }

    public Optional<PlanEntrenamientoResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<PlanEntrenamientoResponseDTO> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::aResponseDTO)
                .collect(Collectors.toList());
    }

    public PlanEntrenamientoResponseDTO desactivar(Long id) {
        PlanEntrenamiento plan = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de desactivar un plan inexistente id={}", id);
                    return new ResourceNotFoundException("El plan de entrenamiento con id " + id + " no existe");
                });
        plan.setActivo(false);
        PlanEntrenamiento actualizado = repository.save(plan);
        log.info("Plan de entrenamiento id={} desactivado correctamente", id);
        return aResponseDTO(actualizado);
    }

    private PlanEntrenamientoResponseDTO aResponseDTO(PlanEntrenamiento plan) {
        return new PlanEntrenamientoResponseDTO(
                plan.getId(),
                plan.getNombrePlan(),
                plan.getEntrenador(),
                plan.getDuracionSemanas(),
                plan.isActivo(),
                plan.getUsuarioId()
        );
    }
}
