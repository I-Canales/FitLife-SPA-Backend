CREATE TABLE equipo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    categoria TEXT NOT NULL,
    estado TEXT NOT NULL DEFAULT 'DISPONIBLE',
    fecha_adquisicion TEXT NOT NULL
);

INSERT INTO equipo (nombre, categoria, estado, fecha_adquisicion) VALUES
('Cinta de correr', 'Cardio', 'DISPONIBLE', '2025-01-15'),
('Bicicleta estática', 'Cardio', 'DISPONIBLE', '2025-02-10'),
('Rack de sentadillas', 'Fuerza', 'DISPONIBLE', '2024-11-20'),
('Máquina de remo', 'Cardio', 'MANTENIMIENTO', '2024-08-05');
