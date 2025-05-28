# 🔄 Diagrama de Secuencia - Sistema de Gestión por Email

## Flujo Principal del Sistema

```mermaid
sequenceDiagram
    participant U as Usuario
    participant G as Gmail/Email
    participant EM as EmailMonitor
    participant EA as EmailAppIndependiente
    participant DB as PostgreSQL
    participant HR as HtmlRes
    participant GR as GmailRelay

    Note over U,GR: Flujo Completo de Procesamiento de Comando

    %% 1. Usuario envía comando
    U->>G: Envía email con comando
    Note right of U: Asunto: "usuario get"

    %% 2. Monitor recibe email
    G->>EM: Detecta nuevo email
    EM->>EM: Extrae remitente y asunto
    
    %% 3. Procesa comando
    EM->>EA: processEmailCommand(sender, subject, content)
    
    %% 4. Verificaciones iniciales
    EA->>DB: TestConnection.testConnection()
    DB-->>EA: Conexión OK/Error
    
    alt Conexión fallida
        EA->>HR: generateError("Conexión DB", mensaje)
        HR-->>EA: HTML de error
        EA->>GR: sendEmail(error)
        GR->>G: Email de error
        G->>U: Recibe error de conexión
    end

    %% 5. Verificar si es comando de registro
    EA->>EA: isRegistrationCommand(subject)
    
    alt Es comando de registro
        EA->>EA: processRegistrationCommand()
        EA->>DB: dUsuario.register(params)
        DB-->>EA: Usuario creado/Error
        EA->>HR: generateSuccess("Registro Exitoso")
        HR-->>EA: HTML de éxito
        EA->>GR: sendEmail(success)
        GR->>G: Email de confirmación
        G->>U: Recibe confirmación de registro
    else No es registro
        %% 6. Verificar autenticación
        EA->>DB: dUsuario.existsByEmail(sender)
        DB-->>EA: true/false
        
        alt Usuario no registrado
            EA->>HR: generateWelcome(email)
            HR-->>EA: HTML de bienvenida
            EA->>GR: sendEmail(welcome)
            GR->>G: Email de bienvenida
            G->>U: Recibe mensaje de registro
        else Usuario registrado
            %% 7. Verificar comando válido
            EA->>EA: isCommandEmail(subject)
            
            alt Comando inválido
                EA->>EA: Ignorar comando
            else Comando válido
                %% 8. Procesar comando
                EA->>EA: processDirectCommand(sender, comando)
                EA->>EA: Parsear entidad, acción y parámetros
                
                alt Comando de usuario
                    EA->>EA: processUsuarioCommand()
                    EA->>DB: nUsuario.get(id) / nUsuario.list()
                    DB-->>EA: Datos de usuario(s)
                    EA->>HR: generateTable(title, headers, data)
                    HR-->>EA: HTML con tabla
                    
                else Comando de producto
                    EA->>EA: processProductoCommand()
                    EA->>DB: dProducto.get(id) / dProducto.list()
                    DB-->>EA: Datos de producto(s)
                    EA->>HR: generateTable(title, headers, data)
                    HR-->>EA: HTML con tabla
                    
                else Comando de categoría
                    EA->>EA: processCategoriaCommand()
                    EA->>DB: dCategoria.get(id) / dCategoria.list()
                    DB-->>EA: Datos de categoría(s)
                    EA->>HR: generateTable(title, headers, data)
                    HR-->>EA: HTML con tabla
                    
                else Comando help
                    EA->>EA: processHelpCommand()
                    EA->>HR: generateTable("Comandos", headers, commandList)
                    HR-->>EA: HTML con ayuda
                end
                
                %% 9. Enviar respuesta
                EA->>GR: sendEmail(response)
                GR->>G: Email con respuesta
                G->>U: Recibe respuesta con datos
            end
        end
    end

    Note over U,GR: Proceso completado
```

## Flujo de Autenticación y Registro

```mermaid
sequenceDiagram
    participant U as Usuario Nuevo
    participant G as Gmail
    participant EA as EmailApp
    participant DB as Database
    participant HR as HtmlRes

    %% Primer intento sin registro
    U->>G: "usuario get"
    G->>EA: Email recibido
    EA->>DB: existsByEmail(sender)
    DB-->>EA: false (no existe)
    EA->>HR: generateWelcome(email)
    HR-->>EA: HTML bienvenida
    EA->>G: Email de bienvenida
    G->>U: "Necesitas registrarte"

    %% Proceso de registro
    U->>G: "registrar Juan Pérez 123456789 masculino"
    G->>EA: Email de registro
    EA->>EA: processRegistrationCommand()
    EA->>DB: register(nombre, apellido, telefono, genero, email)
    
    alt Registro exitoso
        DB-->>EA: Usuario creado
        EA->>HR: generateSuccess("Registro OK")
        HR-->>EA: HTML éxito
        EA->>G: Email confirmación
        G->>U: "Registro exitoso"
    else Error en registro
        DB-->>EA: Error (email duplicado)
        EA->>HR: generateError("Email ya existe")
        HR-->>EA: HTML error
        EA->>G: Email error
        G->>U: "Error en registro"
    end

    %% Comando después de registro
    U->>G: "usuario get"
    G->>EA: Email comando
    EA->>DB: existsByEmail(sender)
    DB-->>EA: true (existe)
    EA->>DB: Ejecutar comando
    DB-->>EA: Datos
    EA->>HR: generateTable(datos)
    HR-->>EA: HTML tabla
    EA->>G: Email respuesta
    G->>U: Datos solicitados
```

## Arquitectura de Componentes

```mermaid
graph TB
    subgraph "Cliente"
        U[Usuario] --> E[Email Client]
    end
    
    subgraph "Servidor Email"
        E --> GM[Gmail Monitor]
        GM --> EA[EmailApp Independiente]
    end
    
    subgraph "Lógica de Negocio"
        EA --> AU[Autenticación]
        EA --> CP[Command Parser]
        EA --> CC[Command Controller]
    end
    
    subgraph "Capa de Datos"
        CC --> DU[DUsuario]
        CC --> DP[DProducto]
        CC --> DC[DCategoria]
        CC --> DCL[DCliente]
        CC --> DT[DTipoPago]
    end
    
    subgraph "Base de Datos"
        DU --> DB[(PostgreSQL)]
        DP --> DB
        DC --> DB
        DCL --> DB
        DT --> DB
    end
    
    subgraph "Generación de Respuesta"
        CC --> HR[HtmlRes]
        HR --> GR[GmailRelay]
        GR --> E
    end
    
    style U fill:#e1f5fe
    style DB fill:#f3e5f5
    style EA fill:#fff3e0
    style HR fill:#e8f5e8
```

## Flujo de Datos

```mermaid
flowchart TD
    START([Usuario envía email]) --> CHECK{Conexión DB OK?}
    
    CHECK -->|No| ERROR1[Enviar error de conexión]
    ERROR1 --> END1([Fin])
    
    CHECK -->|Sí| REG{Es comando registro?}
    
    REG -->|Sí| PROC_REG[Procesar registro]
    PROC_REG --> VALID{Parámetros válidos?}
    VALID -->|No| ERROR2[Error de validación]
    VALID -->|Sí| CREATE[Crear usuario en DB]
    CREATE --> SUCCESS[Enviar confirmación]
    SUCCESS --> END2([Fin])
    ERROR2 --> END3([Fin])
    
    REG -->|No| AUTH{Usuario registrado?}
    
    AUTH -->|No| WELCOME[Enviar mensaje bienvenida]
    WELCOME --> END4([Fin])
    
    AUTH -->|Sí| CMD{Comando válido?}
    
    CMD -->|No| IGNORE[Ignorar]
    IGNORE --> END5([Fin])
    
    CMD -->|Sí| PARSE[Parsear comando]
    PARSE --> EXEC[Ejecutar en DB]
    EXEC --> RESP[Generar respuesta HTML]
    RESP --> SEND[Enviar email]
    SEND --> END6([Fin])
    
    style START fill:#4caf50,color:#fff
    style END1 fill:#f44336,color:#fff
    style END2 fill:#4caf50,color:#fff
    style END3 fill:#f44336,color:#fff
    style END4 fill:#ff9800,color:#fff
    style END5 fill:#9e9e9e,color:#fff
    style END6 fill:#4caf50,color:#fff
```

## Estados del Sistema

```mermaid
stateDiagram-v2
    [*] --> Iniciando
    
    Iniciando --> Conectando_DB
    Conectando_DB --> Listo : Conexión exitosa
    Conectando_DB --> Error_DB : Fallo conexión
    
    Error_DB --> Conectando_DB : Reintentar
    Error_DB --> [*] : Finalizar
    
    Listo --> Recibiendo_Email
    
    Recibiendo_Email --> Validando_Usuario
    
    Validando_Usuario --> Usuario_No_Registrado : Email no existe
    Validando_Usuario --> Usuario_Registrado : Email existe
    Validando_Usuario --> Procesando_Registro : Comando registro
    
    Usuario_No_Registrado --> Enviando_Bienvenida
    Enviando_Bienvenida --> Recibiendo_Email
    
    Procesando_Registro --> Registro_Exitoso : Datos válidos
    Procesando_Registro --> Registro_Fallido : Datos inválidos
    
    Registro_Exitoso --> Enviando_Confirmacion
    Registro_Fallido --> Enviando_Error
    
    Enviando_Confirmacion --> Recibiendo_Email
    Enviando_Error --> Recibiendo_Email
    
    Usuario_Registrado --> Parseando_Comando
    
    Parseando_Comando --> Comando_Valido : Sintaxis correcta
    Parseando_Comando --> Comando_Invalido : Sintaxis incorrecta
    
    Comando_Invalido --> Recibiendo_Email
    
    Comando_Valido --> Ejecutando_Query
    
    Ejecutando_Query --> Query_Exitosa : Datos encontrados
    Ejecutando_Query --> Query_Fallida : Error SQL
    
    Query_Exitosa --> Generando_HTML
    Query_Fallida --> Enviando_Error
    
    Generando_HTML --> Enviando_Respuesta
    Enviando_Respuesta --> Recibiendo_Email
```

---

## 📋 Leyenda de Componentes

| Componente | Función |
|------------|---------|
| **Usuario** | Envía comandos por email |
| **Gmail Monitor** | Detecta nuevos emails |
| **EmailApp** | Procesa comandos y lógica |
| **Database** | Almacena datos (PostgreSQL) |
| **HtmlRes** | Genera respuestas HTML |
| **GmailRelay** | Envía emails de respuesta |

## 🎯 Puntos Clave del Flujo

1. **Autenticación obligatoria** para todos los comandos (excepto registro)
2. **Validación de conexión** antes de procesar
3. **Generación de HTML moderno** para todas las respuestas
4. **Manejo robusto de errores** en cada etapa
5. **Logging detallado** para monitoreo y debugging 