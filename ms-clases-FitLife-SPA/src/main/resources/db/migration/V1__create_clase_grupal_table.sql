CREATE TABLE clase_grupal (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    tipo TEXT NOT NULL,
    horario TEXT NOT NULL,
    cupo_maximo INTEGER NOT NULL,
    entrenador_id INTEGER,
    activa BOOLEAN NOT NULL DEFAULT 1
);

INSERT INTO clase_grupal (nombre, tipo, horario, cupo_maximo, entrenador_id, activa) VALUES
('Zumba', 'Cardio', 'Lunes 18:00', 20, 1, 1),
('Yoga', 'Flexibilidad', 'Martes 08:00', 15, 2, 1),
('Spinning', 'Cardio', 'Miércoles 19:00', 25, 1, 1),
('Crossfit', 'Fuerza', 'Jueves 07:00', 12, 3, 1),
('Pilates', 'Flexibilidad', 'Viernes 17:00', 15, 2, 0);
