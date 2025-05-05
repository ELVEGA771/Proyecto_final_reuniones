FROM openjdk:21-jdk-slim

WORKDIR /app

# Copia tu JAR o las clases compiladas
COPY out/artifacts/ProyectoReuniones_jar/ProyectoReuniones.jar /app/ProyectoReuniones.jar

# Carpeta de configuración (properties, historial, etc.)
COPY recursos/ /app/recursos/

# Por defecto arranca el Main sin argumentos
ENTRYPOINT ["java", "-jar", "ProyectoReuniones.jar"]