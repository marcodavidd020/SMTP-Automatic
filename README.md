# 🚀 Sistema de Email con Comandos CRUD v2.0

**Sistema completo de procesamiento de emails con comandos CRUD automático**

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13%2B-blue.svg)](https://postgresql.org/)
[![JavaMail](https://img.shields.io/badge/JavaMail-1.5%2B-green.svg)](https://javaee.github.io/javamail/)
[![Status](https://img.shields.io/badge/Status-Productivo-brightgreen.svg)]()

---

## 📋 **Índice de Documentación**

### **📚 Documentaciones Principales**

- **[README.md](README.md)** - Esta documentación principal (inicio aquí)
- **[INSTRUCCIONES_SISTEMA_COMPLETO.md](INSTRUCCIONES_SISTEMA_COMPLETO.md)** - Guía del sistema bidireccional completo
- **[DEMO_SISTEMA_COMPLETO.md](DEMO_SISTEMA_COMPLETO.md)** - Demostraciones y casos de uso
- **[README-SERVIDOR-SMTP.md](README-SERVIDOR-SMTP.md)** - Servidor SMTP personalizado
- **[DIAGRAMA_SECUENCIA.md](DIAGRAMA_SECUENCIA.md)** - Diagramas técnicos y flujos

### **🛠️ Scripts de Desarrollo**

- **[compile.sh](compile.sh)** - Script de compilación automatizada
- **[run.sh](run.sh)** - Script de ejecución rápida
- **[start-smtp-ngrok.sh](start-smtp-ngrok.sh)** - Servidor SMTP con túnel ngrok

### **🗂️ Estructura del Proyecto**

- **[sql/init_database.sql](sql/init_database.sql)** - Inicialización de base de datos
- **[lib/](lib/)** - Librerías JavaMail requeridas
- **[src/main/java/](src/main/java/)** - Código fuente principal

---

## 🎯 **¿Qué hace este sistema?**

Este sistema permite **controlar una base de datos PostgreSQL enviando comandos por email**:

### **✅ Características v2.0**

- 🤖 **Monitor Gmail en tiempo real** - Detecta emails nuevos cada 10 segundos
- 📧 **Comandos en ASUNTO y CONTENIDO** - Mayor flexibilidad para usuarios
- 🔄 **Respuestas como REPLY** - Responde al email original como respuesta en secuencia, no crea uno nuevo
- 📝 **Comandos singulares y plurales** - `usuario`/`usuarios`, `producto`/`productos`
- 🏷️ **Nombres descriptivos** - Muestra nombres de categoría, no solo IDs
- 🎨 **Respuestas HTML modernas** - Tablas responsive y diseño profesional
- 🛡️ **Validación completa** - Autenticación, registro automático, sugerencias de error
- 💬 **Threading automático** - Las respuestas aparecen en la misma conversación

### **📧 Comandos Disponibles**

```
• registrar Juan Pérez 123456789 masculino
• usuario get / usuarios get
• producto get / productos get
• categoria get / categorias get
• cliente get / clientes get
• tipo_pago get / tipos_pago get
• help
```

---

## 🚀 **Inicio Rápido**

### **Método 1: Scripts Automatizados (Recomendado)**

```bash
# 1. Compilar todo automáticamente
./compile.sh

# 2. Ejecutar lanzador principal
./run.sh
```

### **Método 2: Comandos Manuales**

```bash
# 1. Compilar manualmente
javac -cp "lib/*:src/main/java" -d . src/main/java/**/*.java

# 2. Ejecutar usando uno de los dos lanzadores:

# Opción A: Lanzador en raíz (más simple)
java -cp "lib/*:." LanzadorPrincipal

# Opción B: Lanzador en tecnoweb (más opciones)
java -cp "lib/*:." com.mycompany.parcial1.tecnoweb.LanzadorPrincipal
```

---

## 🎛️ **Lanzadores Principales**

El sistema incluye **dos lanzadores** diferentes según tus necesidades:

### **🔹 LanzadorPrincipal.java (Raíz) - Simplificado**

**Ubicación:** `LanzadorPrincipal.java`
**Uso:** `java -cp "lib/*:." LanzadorPrincipal`

**Opciones disponibles:**

1. 🤖 **Monitor Gmail** - Procesamiento automático v2.0
2. 🧪 **Test EmailApp** - Pruebas locales
3. 📊 **Información del Sistema** - Configuración y ayuda
4. 🔧 **Configuración avanzada** - Settings

### **🔹 LanzadorPrincipal.java (Tecnoweb) - Completo**

**Ubicación:** `src/main/java/com/mycompany/parcial1/tecnoweb/LanzadorPrincipal.java`
**Uso:** `java -cp "lib/*:." com.mycompany.parcial1.tecnoweb.LanzadorPrincipal`

**Opciones disponibles:**

1. 📧 **EmailApp Tecnoweb** - Versión original POP3
2. 🤖 **Monitor Gmail con Comandos** - Versión v2.0
3. 🌐 **Servidor HTTP Email** - Interfaz web
4. 🔄 **Sistema Completo** - Monitor + HTTP
5. 📊 **Test EmailApp** - Pruebas
6. ❓ **Información completa** - Documentación detallada

---

## 📦 **Instalación Completa**

### **1. Requisitos del Sistema**

```bash
# Java 17+ (requerido)
java -version

# PostgreSQL 13+ (requerido)
psql --version

# Git (para clonar)
git --version
```

### **2. Clonar e Instalar**

```bash
# Clonar repositorio
git clone [tu-repositorio]
cd tecno-mail

# Descargar librerías JavaMail
mkdir -p lib
# Descargar mail-1.5.0-b01.jar y activation-1.1.1.jar a lib/
```

### **3. Configurar Base de Datos**

```bash
# Crear base de datos
sudo -u postgres createdb EcommerceTool

# Inicializar estructura y datos
psql -U postgres -d EcommerceTool -f sql/init_database.sql
```

### **4. Compilar y Ejecutar**

```bash
# Compilar automáticamente
./compile.sh

# Ejecutar
./run.sh
```

---

## 🎮 **Modos de Funcionamiento**

### **🤖 Modo 1: Monitor Gmail (Recomendado)**

**Para procesamiento automático de comandos via email**

```bash
# Desde lanzador simple
./run.sh → Opción 1

# Desde lanzador completo
java -cp "lib/*:." com.mycompany.parcial1.tecnoweb.LanzadorPrincipal → Opción 2

# Directo
java -cp "lib/*:." servidor.GmailMonitorComandos
```

**Funcionalidades:**

- ✅ Monitoreo Gmail en tiempo real
- ✅ Detección de comandos en asunto Y contenido
- ✅ Respuestas como reply al email original
- ✅ Registro automático de usuarios
- ✅ Comandos singulares y plurales

### **🌐 Modo 2: Servidor HTTP**

**Para interfaz web y API REST**

```bash
java -cp "lib/*:." servidor.HTTPEmailServer
```

**Endpoints:**

- `GET /` - Interfaz web para envío
- `POST /send-email` - API REST para aplicaciones

### **🔄 Modo 3: Sistema Completo**

**Monitor Gmail + Servidor HTTP simultáneo**

```bash
java -cp "lib/*:." com.mycompany.parcial1.tecnoweb.LanzadorPrincipal → Opción 4
```

### **🧪 Modo 4: Test Local**

**Pruebas sin envío real de emails**

```bash
./run.sh → Opción 2
```

---

## 📧 **Cómo Usar el Sistema**

### **1. Registrar Usuario**

Envía email a `JairoJairoJairo@gmail.com`:

```
Asunto: registrar Juan Pérez 123456789 masculino
```

### **2. Comandos de Consulta**

```bash
# En asunto o contenido del email:
usuario get              # Lista todos los usuarios
usuarios get             # (Acepta plural también)
producto get 1           # Producto con ID 1
categorias get           # Lista categorías
help                     # Ayuda completa
```

### **3. Responder a Emails del Sistema**

Cuando recibas una respuesta del sistema, **puedes responder ese email** escribiendo un nuevo comando en el contenido:

```
> El 15 dic 2024, Sistema CRUD escribió:
> Aquí están los usuarios solicitados...

productos get
```

El sistema **detectará automáticamente** el comando en tu respuesta.

---

## 💬 **Respuestas como Reply - Nueva Funcionalidad v2.0**

El sistema ahora responde automáticamente como **REPLY** al email original, creando una conversación fluida en lugar de emails separados.

### **🔄 Cómo Funciona**

**✅ ANTES (v1.0):**

```
📧 Tu email: "usuario get"
📧 Sistema crea: Email nuevo "Re: usuario get"
📧 Tu email: "producto get"
📧 Sistema crea: Email nuevo "Re: producto get"
```

**🎉 AHORA (v2.0):**

```
📧 Tu email: "usuario get"
   └── 💬 Sistema responde: Reply en la MISMA conversación
📧 Tu puedes responder directamente escribiendo: "producto get"
   └── 💬 Sistema responde: Reply en la MISMA conversación
```

### **💡 Ventajas del Sistema Reply**

- **🧵 Threading automático** - Todas las consultas en una sola conversación
- **📱 Mejor experiencia móvil** - Fácil seguimiento en apps de email
- **🔍 Historial organizado** - No se llenan las bandejas con emails separados
- **⚡ Respuesta rápida** - Responde directamente al email del sistema
- **🤖 Detección inteligente** - El sistema detecta comandos en respuestas

### **📝 Ejemplos de Uso**

#### **Conversación Completa:**

```
1. 📧 Envías: "help"
   └── 💬 Sistema: Lista de comandos disponibles

2. 💬 Respondes: "usuario get"
   └── 💬 Sistema: Tabla con todos los usuarios

3. 💬 Respondes: "producto get 1"
   └── 💬 Sistema: Detalles del producto ID 1

4. 💬 Respondes: "categorias get"
   └── 💬 Sistema: Lista de categorías
```

#### **Detección de Comandos en Respuestas:**

El sistema detecta comandos en:

- ✅ **Asunto del email original**: `usuario get`
- ✅ **Contenido de respuestas**: Escribes `producto get` como respuesta
- ✅ **Líneas limpias**: Ignora texto citado (líneas con `>`)
- ✅ **Múltiples formatos**: Funciona con email HTML y texto plano

### **🛠️ Implementación Técnica**

El sistema utiliza headers RFC estándar para el threading:

- `In-Reply-To`: Referencia al Message-ID original
- `References`: Cadena de conversación
- `Subject`: Automáticamente prefijado con "Re:"

---

## 🔧 **Configuración Avanzada**

### **📧 Credenciales Gmail**

Editar `src/main/java/servidor/GmailMonitorComandos.java` y `GmailRelay.java`:

```java
private static final String GMAIL_USERNAME = "tu-email@gmail.com";
private static final String GMAIL_APP_PASSWORD = "tu-app-password-16-chars";
```

### **🗄️ Base de Datos**

Editar `src/main/java/postgresConecction/DBConnection.java`:

```java
public static String database = "EcommerceTool";
public static String server = "localhost";
public static String port = "5432";
public static String user = "postgres";
public static String password = "tu-password";
```

### **⏱️ Intervalo de Monitoreo**

En `GmailMonitorComandos.java`:

```java
Thread.sleep(10000); // 10 segundos (actual)
Thread.sleep(5000);  // 5 segundos (más rápido)
```

---

## 📊 **Estructura de Datos**

### **Usuarios**

- id, nombre, apellido, telefono, genero, email, password, rol_id

### **Productos**

- id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria

### **Categorías**

- id, nombre, descripcion

### **Clientes**

- id, nit, direccion, user_id

### **Tipos de Pago**

- id, tipo_pago, created_at

---

## 🛠️ **Desarrollo y Testing**

### **🧪 Testing Automatizado**

```bash
# Test completo de comandos
java -cp "lib/*:." test.TestTodosComandos

# Test específico de usuario
java -cp "lib/*:." test.TestComandoUsuario

# Test directo sin emails
java -cp "lib/*:." TestEmailAppDirecto
```

### **🔍 Debugging**

```bash
# Ejecutar con logs detallados
java -cp "lib/*:." servidor.GmailMonitorComandos 2>&1 | tee logs.txt

# Test de conexión a BD
java -cp "lib/*:." postgresConecction.TestConnection
```

### **📝 Logs del Sistema**

- Conexiones de base de datos
- Emails procesados
- Comandos ejecutados
- Errores detallados

---

## 🚨 **Solución de Problemas**

### **❌ Error de Compilación**

```bash
# Verificar librerías
ls -la lib/
# Debe contener: mail-1.5.0-b01.jar, activation-1.1.1.jar

# Limpiar y recompilar
rm -rf *.class **/*.class
./compile.sh
```

### **❌ Error de Base de Datos**

```bash
# Verificar PostgreSQL
sudo systemctl status postgresql
sudo systemctl start postgresql

# Recrear base de datos
sudo -u postgres dropdb EcommerceTool
sudo -u postgres createdb EcommerceTool
psql -U postgres -d EcommerceTool -f sql/init_database.sql
```

### **❌ Error de Gmail**

1. Verificar credenciales en el código
2. Confirmar contraseña de aplicación (no la normal)
3. Verificar 2FA habilitado en Google
4. Revisar limits de API

### **❌ No Detecta Comandos**

1. Verificar formato exacto de comandos
2. Revisar que el usuario esté registrado
3. Confirmar que el email llegue (revisar spam)
4. Verificar logs del monitor

---

## 🎉 **Casos de Uso Reales**

### **💼 Empresa de Inventario**

```bash
# Consultar productos
productos get

# Ver categorías disponibles
categorias get

# Registrar nuevo empleado
registrar Ana García 987654321 femenino
```

### **🛍️ E-commerce**

```bash
# Revisar catálogo
productos get

# Verificar clientes
clientes get

# Ver métodos de pago
tipos_pago get
```

### **👥 Gestión de Usuarios**

```bash
# Listar todos los usuarios
usuarios get

# Ver usuario específico
usuario get 5

# Ayuda completa
help
```

---

## 🔮 **Funcionalidades Futuras**

### **⏳ En Desarrollo**

- ✅ Comandos de escritura (CREATE, UPDATE, DELETE)
- ✅ Autenticación por roles
- ✅ Upload de imágenes via email
- ✅ Reportes automáticos por email
- ✅ Integración con más bases de datos

### **💡 Propuestas**

- 🔄 Webhooks para eventos
- 📊 Dashboard web en tiempo real
- 🤖 Chatbot integrado
- 📱 Aplicación móvil
- 🔐 OAuth2 para Gmail

---

## 📞 **Soporte y Contacto**

### **👨‍💻 Desarrollador**

- **Nombre:** Jairo Jairo Jairo
- **Email:** JairoJairoJairo@gmail.com
- **Proyecto:** Sistema E-commerce por Email v2.0

### **🐛 Reportar Bugs**

1. Describe el problema detalladamente
2. Incluye logs del error
3. Especifica tu configuración
4. Envía email con pasos para reproducir

### **💡 Sugerencias**

- Nuevos comandos a implementar
- Mejoras en la interfaz
- Optimizaciones de rendimiento
- Integraciones adicionales

---

## 📜 **Licencia y Créditos**

### **📄 Licencia**

Este proyecto está desarrollado para fines educativos y puede ser utilizado libremente.

### **🙏 Créditos**

- **JavaMail API** - Para funcionalidad de email
- **PostgreSQL** - Base de datos principal
- **Gmail API** - Servicio de email
- **ngrok** - Túneles para desarrollo

### **⭐ Agradecimientos**

- Universidad por el proyecto base
- Comunidad PostgreSQL
- Documentación de JavaMail
- Stack Overflow por soluciones

---

## 🚀 **¡Empezar Ahora!**

```bash
# 1. Clona el repositorio
git clone [tu-repositorio]

# 2. Configura la base de datos
sudo -u postgres createdb EcommerceTool
psql -U postgres -d EcommerceTool -f sql/init_database.sql

# 3. Compila y ejecuta
./compile.sh
./run.sh

# 4. Envía tu primer comando
# Email a: JairoJairoJairo@gmail.com
# Asunto: help
```

**¡Tu sistema de e-commerce está listo! 🎉**

---

_Documentación actualizada: Diciembre 2024 | v2.0 | Sistema de E-commerce por Email_
