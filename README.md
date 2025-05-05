# Proyecto final reuniones
### Nombres:
Alex Luna y Andrés Vega

Este proyecto implementa una simulación de un sistema que permite crear reuniones entre empleados y llevar un registro de las mismas, ademas de que notifica a los usuarios participantes. Todo esto implementado en java y para usarlo se utilizó Docker.

## Prerequisitos
* Docker ≥ 20.10
* Docker Compose ≥ 1.29
* (Opcional para compilación local) Java 17+ e IntelliJ IDEA o cualquier otro IDE

## Instalación y compilación
1. Descargar el archivo docker-compose.yml, que tambien esta disponible en GitHub que se puede acceder clonando el repositorio:
```
git clone https://github.com/ELVEGA771/Proyecto_final_reuniones
```
2. Una vez que se tiene el archivo docker-compose.yml, en una terminal se procede a ejecutar el siguiente comando para bajar las imagenes necesarias desde DockerHub:
```
docker-compose pull
```
3. Una vez se tienen los archivos necesarios se levanta los contenedores, usando el siguiente comando el cual solo va a levantar los servidores:
```
docker-compose up servidor-central servidor-alice servidor-bob servidor-carol servidor-david servidor-eva
```
4. Se tiene que abrir otra terminal en la misma carpeta, en la cual se va a ejecutar la aplicación la cual se quiera correr por ejemplo la aplicación de Bob:
```
docker-compose run --rm app-bob
```
## Pruebas del sistema
[![docker-pull.jpg](https://i.postimg.cc/bNjv6QMv/docker-pull.jpg)](https://postimg.cc/BtMsb1JW)
En la imagen se puede observar como, hacer el pull del arhivo docker-compose.yml.

[![levantamiento-servidores.jpg](https://i.postimg.cc/GmLmRbbw/levantamiento-servidores.jpg)](https://postimg.cc/D8NTdkWB)
En esta imagen ya se puede observar como se levantan los servidores incluyendo el servidor central.

[![Probar-bob.jpg](https://i.postimg.cc/R0qC3G3g/Probar-bob.jpg)](https://postimg.cc/JyVCvN5X)
En esta imagen ya se puede ver como se empieza a usar una aplicación en este caso la de Bob la cual despliega un menu principal, indicando la acción que quieres realizar, en este ejemplo se crea una nueva reunion donde se llenan los datos necesarios (si se ingresa mal un dato se vuelve  pedir o regresa al menu), sale un error pero es un error que no afecta el funcionamiento de la aplicación.

[![Probar-david.jpg](https://i.postimg.cc/cLF44PJK/Probar-david.jpg)](https://postimg.cc/TKWXt7gG)
En esta imagen se ejecuta la aplicacion de david, donde se verifica que la reunion agregada por bob tambien le sale a David y que puede modificarla.
