package com.fitlife.fitlifespa.service;

import com.fitlife.fitlifespa.dto.NotificacionRequestDTO;
import com.fitlife.fitlifespa.dto.NotificacionResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.model.Notificacion;
import com.fitlife.fitlifespa.repository.NotificacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    public NotificacionResponseDTO guardar(NotificacionRequestDTO dto) {
        Notificacion n = new Notificacion();
        n.setUsuarioId(dto.getUsuarioId());
        n.setMensaje(dto.getMensaje());
        n.setTipo(dto.getTipo());
        n.setFechaEnvio(dto.getFechaEnvio());
        n.setLeida(false);

        Notificacion guardada = repository.save(n);
        log.info("Notificación creada correctamente id={} para usuarioId={}", guardada.getId(), guardada.getUsuarioId());
        return aResponseDTO(guardada);
    }

    public Optional<NotificacionResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::aResponseDTO);
    }

    public List<NotificacionResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponseDTO).collect(Collectors.toList());
    }

    public NotificacionResponseDTO marcarComoLeida(Long id) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de marcar como leída una notificación inexistente id={}", id);
                    return new ResourceNotFoundException("La notificación con id " + id + " no existe");
                });
        n.setLeida(true);
        Notificacion actualizada = repository.save(n);
        log.info("Notificación id={} marcada como leída", id);
        return aResponseDTO(actualizada);
    }

    private NotificacionResponseDTO aResponseDTO(Notificacion n) {
        return new NotificacionResponseDTO(n.getId(), n.getUsuarioId(), n.getMensaje(), n.getTipo(), n.isLeida(), n.getFechaEnvio());
    }
}
