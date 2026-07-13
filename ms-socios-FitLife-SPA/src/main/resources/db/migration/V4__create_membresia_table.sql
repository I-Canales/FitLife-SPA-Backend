CREATE TABLE membresias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo_plan TEXT NOT NULL,
    fecha_inicio TEXT NOT NULL,
    fecha_fin TEXT NOT NULL,
    precio REAL NOT NULL,
    usuario_id INTEGER NOT NULL
);
