package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.AsistenciaRequestDTO;
import com.fitlife.fitlifespa.dto.AsistenciaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Asistencia;
import com.fitlife.fitlifespa.repository.AsistenciaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsistenciaService {

    private final AsistenciaRepository repository;

    public AsistenciaService(AsistenciaRepository repository) {
        this.repository = repository;
    }

    public AsistenciaResponseDTO registrarEntrada(AsistenciaRequestDTO dto) {
        Asistencia a = new Asistencia();
        a.setUsuarioId(dto.getUsuarioId());
        a.setFecha(dto.getFecha());
        a.setHoraEntrada(dto.getHoraEntrada());

        Asistencia guardada = repository.save(a);
        log.info("Asistencia registrada correctamente id={} para usuarioId={}", guardada.getId(), guardada.getUsuarioId());
        return aResponseDTO(guardada);
    }

    public Optional<AsistenciaResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<AsistenciaResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public AsistenciaResponseDTO registrarSalida(Long id) {
        Asistencia a = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de registrar salida de una asistencia inexistente id={}", id);
                    return new ResourceNotFoundException("El registro de asistencia con id " + id + " no existe");
                });
        a.setHoraSalida(LocalTime.now().toString());
        Asistencia actualizada = repository.save(a);
        log.info("Salida registrada correctamente para asistencia id={}", id);
        return aResponseDTO(actualizada);
    }

    private AsistenciaResponseDTO aResponseDTO(Asistencia a) {
        return new AsistenciaResponseDTO(a.getId(), a.getUsuarioId(), a.getFecha(), a.getHoraEntrada(), a.getHoraSalida());
    }
}
