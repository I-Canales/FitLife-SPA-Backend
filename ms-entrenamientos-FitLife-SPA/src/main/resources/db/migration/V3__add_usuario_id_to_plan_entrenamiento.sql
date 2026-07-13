-- Agrega la relación entre un plan de entrenamiento y el usuario/socio dueño (ms-socios).
-- El valor por defecto 1 permite que los registros ya existentes queden asociados al primer socio de prueba.
ALTER TABLE plan_entrenamiento ADD COLUMN usuario_id INTEGER NOT NULL DEFAULT 1;
