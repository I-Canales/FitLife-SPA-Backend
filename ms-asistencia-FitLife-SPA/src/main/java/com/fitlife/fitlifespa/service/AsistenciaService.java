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

        Asistencia asistencia = crearAsistencia(dto);

        Asistencia asistenciaGuardada = repository.save(asistencia);

        log.info(
                "Asistencia registrada correctamente. id={}, usuarioId={}",
                asistenciaGuardada.getId(),
                asistenciaGuardada.getUsuarioId()
        );

        return convertirDTO(asistenciaGuardada);
    }

    public Optional<AsistenciaResponseDTO> obtenerPorId(Long id) {

        return repository.findById(id)
                .map(this::convertirDTO);
    }

    public List<AsistenciaResponseDTO> obtenerTodos() {

        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    public AsistenciaResponseDTO registrarSalida(Long id) {

        Asistencia asistencia = obtenerAsistenciaPorId(id);

        asistencia.setHoraSalida(LocalTime.now().toString());

        Asistencia asistenciaActualizada = repository.save(asistencia);

        log.info("Salida registrada correctamente para asistencia id={}", id);

        return convertirDTO(asistenciaActualizada);
    }

    private Asistencia crearAsistencia(AsistenciaRequestDTO dto) {

        Asistencia asistencia = new Asistencia();

        asistencia.setUsuarioId(dto.getUsuarioId());
        asistencia.setFecha(dto.getFecha());
        asistencia.setHoraEntrada(dto.getHoraEntrada());

        return asistencia;
    }

    private Asistencia obtenerAsistenciaPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de registrar salida de una asistencia inexistente id={}", id);
                    return new ResourceNotFoundException(
                            "El registro de asistencia con id " + id + " no existe"
                    );
                });
    }

    private AsistenciaResponseDTO convertirDTO(Asistencia asistencia) {

        return new AsistenciaResponseDTO(
                asistencia.getId(),
                asistencia.getUsuarioId(),
                asistencia.getFecha(),
                asistencia.getHoraEntrada(),
                asistencia.getHoraSalida()
        );
    }
}