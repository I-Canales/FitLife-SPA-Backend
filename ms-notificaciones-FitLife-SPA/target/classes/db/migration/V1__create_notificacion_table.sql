CREATE TABLE notificacion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    mensaje TEXT NOT NULL,
    tipo TEXT NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT 0,
    fecha_envio TEXT NOT NULL
);

INSERT INTO notificacion (usuario_id, mensaje, tipo, leida, fecha_envio) VALUES
(1, 'Tu membresía vence en 3 días', 'RECORDATORIO', 0, '2026-07-01'),
(2, 'Tu clase de Zumba fue reprogramada', 'AVISO', 1, '2026-06-28'),
(3, 'Bienvenido a FitLife SPA', 'BIENVENIDA', 1, '2026-06-01');
