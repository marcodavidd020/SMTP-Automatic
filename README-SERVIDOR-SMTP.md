# 🚀 Servidor SMTP Personalizado

Este proyecto implementa tu **propio servidor SMTP** que actúa como relay usando Gmail como backend. Permite recibir correos en direcciones como `usuario@mi-servidor.local` y enviarlos a través de Gmail.

## 📋 Características

- ✅ **Servidor SMTP completo** en puerto 2525
- ✅ **Relay a través de Gmail** (no dependes de tecnoweb.org.bo)
- ✅ **Direcciones personalizadas** como `user@mi-servidor.local`
- ✅ **Manejo de múltiples clientes** simultáneos
- ✅ **Headers personalizados** para identificar el origen

## 🛠️ Configuración

### 1. Configurar credenciales de Gmail

Edita `src/main/java/servidor/GmailRelay.java`:

```java
// ⚠️ CAMBIAR ESTAS CREDENCIALES POR LAS TUYAS:
private static final String GMAIL_USERNAME = "tu-email@gmail.com";
private static final String GMAIL_APP_PASSWORD = "tu-app-password-16-chars";
```

### 2. Obtener contraseña de aplicación de Google

1. Ve a [myaccount.google.com](https://myaccount.google.com) → **Seguridad**
2. Activa la **verificación en 2 pasos**
3. Busca **"Contraseñas de aplicaciones"**
4. Genera una nueva para **"Correo"**
5. Usa esa contraseña de 16 caracteres

## 🚀 Uso

### Iniciar el servidor

```bash
# Compilar
javac -cp "lib/*:src" src/main/java/servidor/*.java

# Ejecutar servidor
java -cp "lib/*:src/main/java" servidor.SMTPServer
```

Verás:
```
🚀 Servidor SMTP iniciado en puerto 2525
📧 Dominio del servidor: mi-servidor.local
💡 Usa direcciones como: usuario@mi-servidor.local
⏳ Esperando conexiones...
```

### Probar con el cliente incluido

En otra terminal:
```bash
# Ejecutar cliente de prueba
java -cp "lib/*:src/main/java" servidor.SMTPClient
```

### Usar desde tu aplicación existente

Modifica `EmailSend.java` para usar tu servidor:

```java
private final static String PORT_SMTP = "2525";
private final static String HOST = "localhost"; // o la IP de tu servidor
private final static String MAIL = "usuario@mi-servidor.local";
```

## 📧 Cómo funciona

1. **Cliente conecta** al puerto 2525
2. **Servidor recibe** el email con dirección `user@mi-servidor.local`
3. **GmailRelay procesa** el mensaje y lo envía vía Gmail
4. **Destinatario recibe** email desde tu Gmail con headers personalizados

## 🔧 Estructura del proyecto

```
src/main/java/servidor/
├── SMTPServer.java        # Servidor principal
├── SMTPClientHandler.java # Manejo de conexiones
├── GmailRelay.java       # Relay a Gmail
└── SMTPClient.java       # Cliente de prueba
```

## 🌐 Configuración de red

### Para acceso externo

1. **Cambiar IP en SMTPServer.java:**
```java
serverSocket = new ServerSocket(SMTP_PORT, 50, InetAddress.getByName("0.0.0.0"));
```

2. **Abrir puerto en firewall:**
```bash
sudo ufw allow 2525/tcp
```

3. **Configurar dominio:** Apuntar tu dominio a la IP del servidor

### Para uso interno

- Solo cambia `localhost` por la IP del servidor en los clientes
- Funciona en red local sin configuración adicional

## 📝 Ejemplo de uso

```java
// Tu aplicación puede enviar así:
EmailSend sender = new EmailSend();
sender.configure("localhost", 2525, "admin@mi-servidor.local");
sender.send("destinatario@example.com", "Asunto", "Mensaje");
```

El destinatario recibirá:
- **From:** tu-gmail@gmail.com
- **Reply-To:** admin@mi-servidor.local  
- **Subject:** [Mi-Servidor] Asunto
- **Headers:** X-Original-From, X-Server

## 🔒 Seguridad

- ✅ Usa TLS para Gmail
- ✅ No guarda credenciales en logs
- ⚠️ Considera agregar autenticación SMTP para producción
- ⚠️ Implementar rate limiting si es necesario

## 🐛 Solución de problemas

### Error "Connection refused"
- Verifica que el servidor esté ejecutándose
- Confirma el puerto 2525 esté libre
- Revisa firewall si es acceso remoto

### Error de Gmail "Authentication failed"
- Verifica las credenciales en `GmailRelay.java`
- Confirma que la contraseña sea de aplicación (no la normal)
- Revisa que 2FA esté habilitado en Google

### Emails no llegan
- Revisa logs del servidor para errores
- Confirma que Gmail esté enviando correctamente
- Verifica carpeta de spam del destinatario

¡Tu servidor SMTP está listo! 🎉 