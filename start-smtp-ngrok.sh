#!/bin/bash

# 🚀 Script para iniciar servidor SMTP con ngrok
# Autor: Jairo

echo "🚀 Iniciando servidor SMTP con ngrok..."
echo "================================================"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para mostrar mensajes coloreados
print_step() {
    echo -e "${GREEN}[PASO $1]${NC} $2"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Paso 1: Verificar dependencias
print_step "1" "Verificando dependencias..."

if ! command -v javac &> /dev/null; then
    print_error "Java compiler (javac) no encontrado"
    exit 1
fi

if ! command -v ngrok &> /dev/null; then
    print_error "ngrok no encontrado"
    echo "Instala ngrok desde: https://ngrok.com/"
    exit 1
fi

echo "✅ Java y ngrok encontrados"

# Paso 2: Compilar código
print_step "2" "Compilando código Java..."

if [ -d "lib" ]; then
    CLASSPATH="lib/*:src/main/java"
else
    CLASSPATH="src/main/java"
fi

javac -cp "$CLASSPATH" src/main/java/servidor/*.java 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ Código compilado exitosamente"
else
    print_error "Error compilando código"
    exit 1
fi

# Paso 3: Verificar configuración de Gmail
print_step "3" "Verificando configuración de Gmail..."

GMAIL_CONFIG=$(grep "GMAIL_USERNAME.*gmail.com" src/main/java/servidor/GmailRelay.java)
if [[ $GMAIL_CONFIG == *"tu-email@gmail.com"* ]]; then
    print_warning "¡Necesitas configurar tus credenciales de Gmail!"
    echo "Edita src/main/java/servidor/GmailRelay.java"
    echo "Cambia GMAIL_USERNAME y GMAIL_APP_PASSWORD"
    echo ""
    read -p "¿Ya configuraste Gmail? (y/n): " gmail_ready
    if [[ $gmail_ready != "y" && $gmail_ready != "Y" ]]; then
        echo "❌ Configura Gmail primero y vuelve a ejecutar"
        exit 1
    fi
fi

echo "✅ Configuración verificada"

# Paso 4: Crear archivos temporales para logs
LOG_DIR="logs"
mkdir -p "$LOG_DIR"

# Paso 5: Función para limpiar procesos al salir
cleanup() {
    echo ""
    print_step "CLEANUP" "Cerrando procesos..."
    
    if [ ! -z "$NGROK_PID" ]; then
        kill $NGROK_PID 2>/dev/null
    fi
    
    if [ ! -z "$SERVER_PID" ]; then
        kill $SERVER_PID 2>/dev/null
    fi
    
    echo "🛑 Procesos cerrados"
    exit 0
}

trap cleanup SIGINT SIGTERM

# Paso 6: Iniciar ngrok en background
print_step "4" "Iniciando ngrok..."

ngrok tcp 2525 > "$LOG_DIR/ngrok.log" 2>&1 &
NGROK_PID=$!

echo "🔄 Esperando que ngrok se inicie..."
sleep 3

# Intentar obtener la URL de ngrok
NGROK_URL=""
for i in {1..10}; do
    NGROK_URL=$(curl -s http://localhost:4040/api/tunnels 2>/dev/null | grep -o '"public_url":"[^"]*' | grep tcp | cut -d'"' -f4)
    if [ ! -z "$NGROK_URL" ]; then
        break
    fi
    sleep 1
done

if [ -z "$NGROK_URL" ]; then
    print_warning "No se pudo obtener la URL de ngrok automáticamente"
    echo "Revisa manualmente en: http://localhost:4040"
    NGROK_HOST="[URL-NGROK]"
    NGROK_PORT="[PUERTO-NGROK]"
else
    # Extraer host y puerto de la URL
    NGROK_HOST=$(echo $NGROK_URL | sed 's|tcp://||' | cut -d':' -f1)
    NGROK_PORT=$(echo $NGROK_URL | sed 's|tcp://||' | cut -d':' -f2)
    echo "✅ ngrok iniciado: $NGROK_URL"
fi

# Paso 7: Mostrar información de conexión
print_step "5" "Información de conexión:"

echo "================================================"
echo "🌐 URL PÚBLICA: $NGROK_URL"
echo "🖥️  HOST: $NGROK_HOST"
echo "🔌 PUERTO: $NGROK_PORT"
echo "📧 EMAIL: admin@mi-servidor.ngrok.io"
echo "================================================"
echo ""
echo "📋 Para conectar desde cualquier lugar usa:"
echo "   HOST: $NGROK_HOST"
echo "   PORT: $NGROK_PORT"
echo ""
echo "💡 Dashboard de ngrok: http://localhost:4040"
echo "================================================"

# Paso 8: Iniciar servidor SMTP
print_step "6" "Iniciando servidor SMTP..."

java -cp "$CLASSPATH" servidor.SMTPServerNgrok "$NGROK_HOST.ngrok.io" &
SERVER_PID=$!

echo ""
echo "🎉 ¡Servidor SMTP con ngrok iniciado!"
echo "⚠️  Presiona Ctrl+C para detener"
echo ""

# Esperar a que termine
wait $SERVER_PID 