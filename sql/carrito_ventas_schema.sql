-- 🛒 SISTEMA DE CARRITO Y VENTAS
-- Script SQL para crear las tablas necesarias para e-commerce

-- 1. Tabla ProductoAlmacen (para stock)
CREATE TABLE IF NOT EXISTS ProductoAlmacen (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER REFERENCES productos(id),
    stock INTEGER NOT NULL DEFAULT 0,
    stock_minimo INTEGER DEFAULT 5,
    ubicacion VARCHAR(100),
    fecha_actualizacion DATE DEFAULT CURRENT_DATE
);

-- 2. Tabla Carrito
CREATE TABLE IF NOT EXISTS Carrito (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES clientes(id),
    fecha DATE DEFAULT CURRENT_DATE,
    total DECIMAL(10,2) DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'activo'
);

-- 3. Tabla Detalle_carrito
CREATE TABLE IF NOT EXISTS Detalle_carrito (
    id SERIAL PRIMARY KEY,
    carrito_id INTEGER REFERENCES Carrito(id) ON DELETE CASCADE,
    producto_id INTEGER REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    total DECIMAL(10,2) NOT NULL
);

-- 4. Tabla NotaVenta
CREATE TABLE IF NOT EXISTS NotaVenta (
    id SERIAL PRIMARY KEY,
    fecha DATE DEFAULT CURRENT_DATE,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente',
    cliente_id INTEGER REFERENCES clientes(id)
);

-- 5. Tabla Detalle_Venta
CREATE TABLE IF NOT EXISTS Detalle_Venta (
    id SERIAL PRIMARY KEY,
    nota_venta_id INTEGER REFERENCES NotaVenta(id) ON DELETE CASCADE,
    producto_id INTEGER REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    total DECIMAL(10,2) NOT NULL
);

-- 6. Crear tabla pago si no existe
CREATE TABLE IF NOT EXISTS pago (
    id SERIAL PRIMARY KEY,
    fechapago DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'pendiente',
    pago_facil_id VARCHAR(100),
    tipo_pago_id INTEGER REFERENCES tipos_pago(id),
    nota_venta_id INTEGER REFERENCES NotaVenta(id)
);

-- 🌱 DATOS DE PRUEBA
INSERT INTO ProductoAlmacen (producto_id, stock, stock_minimo) 
SELECT id, 50, 5 FROM productos 
ON CONFLICT DO NOTHING;

SELECT 'Tablas de carrito y ventas creadas exitosamente' as resultado; 