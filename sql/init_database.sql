-- Script de inicialización de tablas para EcommerceTool
-- Usar en base de datos existente: EcommerceTool
-- Servidor: localhost:5432
-- Usuario: postgres

-- Este script asume que la base de datos EcommerceTool ya existe
-- Solo crea las tablas y datos iniciales

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de usuarios (equivalente a users en Laravel)
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    genero VARCHAR(10),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INTEGER REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de clientes (relacionada con usuarios)
CREATE TABLE IF NOT EXISTS clientes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES usuarios(id) ON DELETE CASCADE,
    nit VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    cod_producto VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    imagen VARCHAR(500),
    descripcion TEXT,
    categoria_id INTEGER REFERENCES categorias(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de tipos de pago
CREATE TABLE IF NOT EXISTS tipos_pago (
    id SERIAL PRIMARY KEY,
    tipo_pago VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de promociones
CREATE TABLE IF NOT EXISTS promociones (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    descuento TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de promoción_productos (relación muchos a muchos)
CREATE TABLE IF NOT EXISTS promocion_productos (
    id SERIAL PRIMARY KEY,
    promocion_id INTEGER REFERENCES promociones(id) ON DELETE CASCADE,
    producto_id INTEGER REFERENCES productos(id) ON DELETE CASCADE,
    descuento_porcentaje DECIMAL(5,2),
    descuento_fijo DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(promocion_id, producto_id)
);

-- Tabla de servicios
CREATE TABLE IF NOT EXISTS servicios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de eventos
CREATE TABLE IF NOT EXISTS eventos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    capacidad INTEGER,
    precio_entrada DECIMAL(10,2),
    fecha DATE,
    hora TIME,
    ubicacion VARCHAR(200),
    estado VARCHAR(50) DEFAULT 'activo',
    imagen VARCHAR(500),
    servicio_id INTEGER REFERENCES servicios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de reservas
CREATE TABLE IF NOT EXISTS reservas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    fecha DATE NOT NULL,
    costo_entrada DECIMAL(10,2),
    cantidad INTEGER,
    costo_total DECIMAL(10,2),
    estado VARCHAR(50) DEFAULT 'pendiente',
    usuario_id INTEGER REFERENCES usuarios(id),
    evento_id INTEGER REFERENCES eventos(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS pagos (
    id SERIAL PRIMARY KEY,
    monto DECIMAL(10,2) NOT NULL,
    fecha DATE NOT NULL,
    metodo_pago VARCHAR(50),
    reserva_id INTEGER REFERENCES reservas(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de patrocinadores
CREATE TABLE IF NOT EXISTS patrocinadores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    email VARCHAR(100),
    telefono VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de patrocinios
CREATE TABLE IF NOT EXISTS patrocinios (
    id SERIAL PRIMARY KEY,
    aporte DECIMAL(10,2),
    patrocinador_id INTEGER REFERENCES patrocinadores(id),
    evento_id INTEGER REFERENCES eventos(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de detalle de eventos (relación muchos a muchos entre eventos y servicios)
CREATE TABLE IF NOT EXISTS detalle_eventos (
    evento_id INTEGER REFERENCES eventos(id),
    servicio_id INTEGER REFERENCES servicios(id),
    costo_servicio DECIMAL(10,2),
    PRIMARY KEY (evento_id, servicio_id)
);

-- Insertar datos iniciales
INSERT INTO roles (nombre) VALUES 
    ('admin'),
    ('cliente'),
    ('organizador') 
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO servicios (nombre, descripcion) VALUES 
    ('Catering', 'Servicio de comida y bebida'),
    ('Decoración', 'Decoración del evento'),
    ('Sonido', 'Sistema de sonido profesional'),
    ('Iluminación', 'Sistema de iluminación'),
    ('Seguridad', 'Servicio de seguridad del evento')
ON CONFLICT DO NOTHING;

INSERT INTO usuarios (nombre, apellido, telefono, genero, email, password, rol_id) VALUES 
    ('Admin', 'Sistema', '123456789', 'M', 'admin@sistema.com', 'admin123', 1),
    ('Jairo Jairo', 'Jairo', '987654321', 'M', 'JairoJairoJairo@gmail.com', 'Jairo123', 1),
    ('Usuario', 'Prueba', '555123456', 'M', 'test@test.com', 'test123', 2)
ON CONFLICT (email) DO NOTHING;

-- Insertar categorías de ejemplo
INSERT INTO categorias (nombre, descripcion) VALUES 
    ('Electrónicos', 'Productos electrónicos y tecnológicos'),
    ('Ropa', 'Vestimenta y accesorios'),
    ('Hogar', 'Productos para el hogar'),
    ('Deportes', 'Artículos deportivos'),
    ('Libros', 'Libros y material educativo')
ON CONFLICT DO NOTHING;

-- Insertar tipos de pago
INSERT INTO tipos_pago (tipo_pago) VALUES 
    ('Efectivo'),
    ('Tarjeta de Crédito'),
    ('Tarjeta de Débito'),
    ('Transferencia Bancaria'),
    ('QR'),
    ('PayPal')
ON CONFLICT DO NOTHING;

-- Insertar productos de ejemplo
INSERT INTO productos (cod_producto, nombre, precio_compra, precio_venta, descripcion, categoria_id) VALUES 
    ('PROD001', 'Smartphone Samsung A54', 250.00, 350.00, 'Teléfono inteligente con 128GB de almacenamiento', 1),
    ('PROD002', 'Laptop HP Pavilion', 800.00, 1200.00, 'Laptop para uso profesional y gaming', 1),
    ('PROD003', 'Camiseta Adidas', 15.00, 25.00, 'Camiseta deportiva de algodón', 2),
    ('PROD004', 'Pantalón Jeans', 30.00, 50.00, 'Pantalón de mezclilla azul', 2),
    ('PROD005', 'Aspiradora Electrolux', 150.00, 220.00, 'Aspiradora para el hogar', 3)
ON CONFLICT (cod_producto) DO NOTHING;

-- Insertar clientes de ejemplo
INSERT INTO clientes (user_id, nit) VALUES 
    (2, '12345678901'),
    (3, '98765432109')
ON CONFLICT (nit) DO NOTHING;

-- Insertar promociones de ejemplo
INSERT INTO promociones (nombre, fecha_inicio, fecha_fin, descuento) VALUES 
    ('Black Friday 2024', '2024-11-25', '2024-11-30', 'Hasta 50% de descuento'),
    ('Fin de Año', '2024-12-20', '2024-12-31', 'Descuentos especiales'),
    ('Vuelta a Clases', '2024-02-01', '2024-02-28', '20% en productos educativos')
ON CONFLICT DO NOTHING;

-- Mostrar información de las tablas creadas
\dt

-- Mostrar algunos datos de prueba
SELECT 'Roles creados:' as info;
SELECT * FROM roles;

SELECT 'Usuarios creados:' as info;
SELECT id, nombre, apellido, email, rol_id FROM usuarios;

SELECT 'Categorías creadas:' as info;
SELECT * FROM categorias;

SELECT 'Productos creados:' as info;
SELECT id, cod_producto, nombre, precio_venta, categoria_id FROM productos;

SELECT 'Tipos de pago creados:' as info;
SELECT * FROM tipos_pago;

SELECT 'Promociones creadas:' as info;
SELECT id, nombre, fecha_inicio, fecha_fin FROM promociones;

COMMIT; 