CREATE TABLE pago (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    monto REAL NOT NULL,
    fecha_pago TEXT NOT NULL,
    metodo_pago TEXT NOT NULL,
    estado TEXT NOT NULL DEFAULT 'PAGADO'
);

INSERT INTO pago (usuario_id, monto, fecha_pago, metodo_pago, estado) VALUES
(1, 29990, '2026-06-01', 'Tarjeta de crédito', 'PAGADO'),
(2, 19990, '2026-06-01', 'Transferencia', 'PAGADO'),
(3, 29990, '2026-06-05', 'Efectivo', 'PAGADO'),
(1, 29990, '2026-05-01', 'Tarjeta de crédito', 'ANULADO');
