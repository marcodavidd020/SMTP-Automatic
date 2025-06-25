#!/bin/bash

# 🚀 Script de Compilación - Sistema de Email Personalizado
# Autor: Jairo Jairo Jairo

echo "🚀 Sistema de Email Personalizado - Compilación"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Verificar Java
if ! command -v javac &> /dev/null; then
    echo "❌ Error: Java no está instalado o no está en el PATH"
    exit 1
fi

echo "✅ Java encontrado: $(java -version 2>&1 | head -n 1)"

# Crear directorio lib si no existe
if [ ! -d "lib" ]; then
    echo "📁 Creando directorio lib..."
    mkdir -p lib
fi

# Verificar librerías
if [ ! -f "lib/mail-1.5.0-b01.jar" ]; then
    echo "⚠️ Advertencia: mail-1.5.0-b01.jar no encontrado en lib/"
    echo "💡 Descárgalo de: https://javaee.github.io/javamail/"
fi

if [ ! -f "lib/activation-1.1.1.jar" ]; then
    echo "⚠️ Advertencia: activation-1.1.1.jar no encontrado en lib/"
    echo "💡 Descárgalo de: https://github.com/javaee/activation"
fi

# Compilar
echo "🔧 Compilando archivos Java..."

# Compilar todas las clases
find src/main/java -name "*.java" -exec javac -cp "lib/*:src/main/java" -d . {} +

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa!"
    
    echo ""
    echo "🎯 OPCIONES DE EJECUCIÓN:"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "1. 🚀 Lanzador Principal (Recomendado):"
    echo "   java -cp \"lib/*:.\" com.mycompany.parcial1.tecnoweb.LanzadorPrincipal"
    echo ""
    echo "2. 🤖 Monitor Gmail con Comandos:"
    echo "   java -cp \"lib/*:.\" servidor.GmailMonitorComandos"
    echo ""
    echo "3. 🌐 Servidor HTTP:"
    echo "   java -cp \"lib/*:.\" servidor.HTTPEmailServer"
    echo ""
    echo "4. 📊 Test EmailApp Independiente:"
    echo "   java -cp \"lib/*:.\" com.mycompany.parcial1.tecnoweb.EmailAppIndependiente"
    echo ""
    echo "💡 Para facilitar, ejecuta: ./run.sh"
    
    # Crear script de ejecución
    cat > run.sh << 'EOF'
#!/bin/bash
echo "🚀 Iniciando Lanzador Principal..."
java -cp "lib/*:." com.mycompany.parcial1.tecnoweb.LanzadorPrincipal
EOF
    chmod +x run.sh
    echo "✅ Script run.sh creado"
    
else
    echo "❌ Error en la compilación"
    echo ""
    echo "🔧 POSIBLES SOLUCIONES:"
    echo "1. Verificar que las librerías estén en lib/"
    echo "2. Verificar que Java esté correctamente instalado"
    echo "3. Revisar errores de sintaxis en el código"
    exit 1
fi

echo ""
echo "📋 ARCHIVOS NECESARIOS:"
echo "✅ lib/mail-1.5.0-b01.jar"
echo "✅ lib/activation-1.1.1.jar"
echo "✅ Base de datos EcommerceTool (debe existir)"
echo ""
echo "🎉 ¡Listo para ejecutar!" 