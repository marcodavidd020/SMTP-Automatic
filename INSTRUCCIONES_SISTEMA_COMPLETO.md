# 📧 Sistema de Email Bidireccional Completo

## 🎯 **¿Qué hace este sistema?**

Este es un **servidor de email completamente independiente** que permite:

### 📤 **ENVIAR EMAILS:**
- ✅ Interfaz web para enviar emails
- ✅ API HTTP para aplicaciones
- ✅ Cliente Java para integración

### 📥 **RECIBIR Y RESPONDER AUTOMÁTICAMENTE:**
- ✅ Monitorea tu Gmail cada 30 segundos
- ✅ Detecta emails nuevos automáticamente
- ✅ Responde con información completa del proyecto
- ✅ Evita loops de respuestas automáticas

---

## 🚀 **Cómo usar el sistema:**

### **1. Iniciar el servidor completo:**
```bash
java -cp "lib/*:src/main/java" servidor.EmailServerComplete
```

### **2. Con ngrok (en otra terminal):**
```bash
ngrok http 8080
```

---

## 📨 **Cómo otros usuarios pueden contactarte:**

### **Opción 1: Email directo**
Cualquier persona puede enviarte un email a: **marcodavidtoledo@gmail.com**

**¿Qué pasará?**
1. 📧 Tu sistema detecta el email automáticamente
2. 🤖 Responde con información completa del proyecto
3. 📋 Incluye tu contacto, tecnologías, y características
4. ⚡ Todo en menos de 30 segundos

### **Opción 2: Formulario web**
Pueden usar tu interfaz web: **https://tu-url-ngrok.app**

---

## 🔄 **Flujo completo de comunicación:**

```
1. Usuario envía email → marcodavidtoledo@gmail.com
                    ↓
2. Tu sistema detecta el email nuevo (30 seg max)
                    ↓
3. Procesa el contenido automáticamente
                    ↓
4. Genera respuesta con info del proyecto
                    ↓
5. Envía respuesta automática al usuario
                    ↓
6. Usuario recibe información completa
```

---

## 📋 **Respuesta automática incluye:**

- 🚀 **Información del proyecto**
- 👨‍💻 **Tus datos de contacto**
- 🔧 **Características técnicas**
- 📊 **Tecnologías utilizadas**
- 🎯 **Contenido del mensaje original**
- 🌐 **URL para probar el servidor**

---

## 🧪 **Para probar el sistema:**

### **Test 1: Envío manual**
1. Ve a: https://tu-url-ngrok.app
2. Llena el formulario
3. Verifica que llega el email

### **Test 2: Respuesta automática**
1. Envía un email desde otra cuenta a: marcodavidtoledo@gmail.com
2. Espera máximo 30 segundos
3. Revisa que recibiste respuesta automática

---

## ⚙️ **Configuración avanzada:**

### **Cambiar intervalo de monitoreo:**
En `GmailMonitor.java`, línea con `Thread.sleep(30000)`:
```java
Thread.sleep(10000); // 10 segundos
Thread.sleep(60000); // 60 segundos
```

### **Personalizar respuesta automática:**
Edita el método `generateAutoReply()` en `GmailMonitor.java`

### **Cambiar credenciales:**
Modifica las constantes en `GmailMonitor.java` y `GmailRelay.java`

---

## 🛡️ **Características de seguridad:**

- ✅ **Evita loops**: No responde a emails automáticos
- ✅ **Filtra spam**: Detecta patrones de emails automáticos
- ✅ **Logs detallados**: Monitorea toda la actividad
- ✅ **Conexión segura**: TLS/SSL para Gmail

---

## 📊 **Monitoreo del sistema:**

El sistema muestra cada minuto:
- 📤 Estado del servidor HTTP
- 📥 Estado del monitor Gmail
- 🌐 URL pública actual
- 📧 Email monitoreado

---

## 🎉 **¡Tu sistema está listo!**

Ahora tienes un **servidor de email completamente funcional** que:
- ✅ Es independiente de tecnoweb.org.bo
- ✅ Funciona 24/7
- ✅ Responde automáticamente
- ✅ Está accesible globalmente
- ✅ Es completamente personalizable

**¡Comparte tu URL ngrok y ya puedes recibir emails de cualquier persona!** 🚀 