CREATE TABLE registro_progreso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    fecha TEXT NOT NULL,
    peso REAL NOT NULL,
    grasa_corporal REAL,
    observaciones TEXT
);

INSERT INTO registro_progreso (usuario_id, fecha, peso, grasa_corporal, observaciones) VALUES
(1, '2026-06-01', 82.0, 22.5, 'Inicio del programa'),
(1, '2026-07-01', 79.5, 20.1, 'Buen progreso este mes'),
(2, '2026-06-15', 65.0, 18.0, 'Primera medición');
