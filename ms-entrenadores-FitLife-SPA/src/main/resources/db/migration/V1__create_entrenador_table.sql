CREATE TABLE entrenador (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    especialidad TEXT NOT NULL,
    telefono TEXT NOT NULL,
    email TEXT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT 1
);

INSERT INTO entrenador (nombre, especialidad, telefono, email, activo) VALUES
('Diego Fuentes', 'Crossfit', '+56911111111', 'diego.fuentes@fitlife.com', 1),
('Camila Rojas', 'Yoga', '+56922222222', 'camila.rojas@fitlife.com', 1),
('Matías Herrera', 'Spinning', '+56933333333', 'matias.herrera@fitlife.com', 1),
('Francisca Vidal', 'Pilates', '+56944444444', 'francisca.vidal@fitlife.com', 0);
