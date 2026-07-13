CREATE TABLE plan_entrenamiento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_plan TEXT NOT NULL, -- Ej: Plan Spinning, Plan Yoga
    entrenador TEXT NOT NULL,
    duracion_semanas INTEGER NOT NULL
);