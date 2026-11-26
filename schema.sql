CREATE TABLE IF NOT EXISTS envios (
    guia TEXT PRIMARY KEY,
    tipo TEXT NOT NULL,
    remitente TEXT NOT NULL,
    destinatario TEXT NOT NULL,
    destino TEXT NOT NULL,
    peso_real REAL NOT NULL,
    largo REAL NOT NULL,
    ancho REAL NOT NULL,
    alto REAL NOT NULL,
    valor_declarado REAL NOT NULL,
    estado TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS historial_estados (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    guia TEXT NOT NULL,
    estado_anterior TEXT,
    estado_nuevo TEXT NOT NULL,
    fecha_hora TEXT NOT NULL,
    FOREIGN KEY (guia) REFERENCES envios(guia) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_historial_guia ON historial_estados(guia);
