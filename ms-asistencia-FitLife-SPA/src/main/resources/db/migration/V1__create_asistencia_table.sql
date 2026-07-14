CREATE TABLE asistencia (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    fecha TEXT NOT NULL,
    hora_entrada TEXT NOT NULL,
    hora_salida TEXT
);

INSERT INTO asistencia (usuario_id, fecha, hora_entrada, hora_salida) VALUES
(1, '2026-07-01', '08:30', '10:00'),
(2, '2026-07-01', '18:00', NULL),
(3, '2026-06-30', '07:15', '08:45');
