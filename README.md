# SIMONE
SIMONE (Sistema de Monitoreo Neonatal) es una aplicación para dispositivos móviles Android cuyas principales funcionalidades son las siguientes:
  * **Registro** de pacientes
  * Realización de **chequeos** sobre los pacientes, completando una lista de verificación de seguridad (LVS)
  * Acceso a los últimos chequeos realizados sobre un paciente

## Cómo probar la aplicación
1. Clona este repositorio: 
```bash
git clone https://github.com/fedeq/simone
```
2. [Crea un nuevo proyecto en Firebase](https://console.firebase.google.com/)
3. Crea una base de datos en Firebase (**Realtime database**)
4. En la sección "Reglas" de tu base de datos, reemplaza el contenido por lo siguiente y haz click en "publicar":
```json
{
  "rules": {
    ".read": "true",
    ".write": "true",
  	"pacientes": {
      ".indexOn": ["estado"]
    },
    "alertas" : {
      ".indexOn" : ["uid"]
    }
  }
}
```
5. Habilita la **autenticación** mediante "Correo electrónico/contraseña" y crea un usuario de prueba con el correo y contraseña que quieras (no es necesario que el correo sea real).
6. Ingresa a la "Configuración de tu proyecto" y agrega una nueva App android en la seccion "Tus apps
" de la pestaña "general".
  * **Nombre del paquete:** com.project.fedeq.simone
  * **Sobrenombre:** Simone
  * **Certificado de firma SHA-1:** [Ingresa aqui para ver como obtenerlo](https://developers.google.com/android/guides/client-auth)
7. Descarga el archivo **google-services.json** y colocalo en la carpeta app/ del proyecto clonado en el paso 1
8. Ya estarias listo para compilar y ejecutar tu aplicación desde **Android Studio**

## Información adicional del proyecto
El proyecto fue desarrollado como Tesis de Licenciatura En Ciencias de la Computación por:
  * Matias Lameiro
  * Federico Quattrocchio
  * Martin Larrea (Director de Tesis)
  * Equipo de neonatología del Hospital J. Penna (Bahía Blanca)
  
La misma puede ser descargada desde el siguiete [link](https://docs.google.com/document/d/1BMdFgCEQIVN36odTSPoPNXXwVMfJzJJOY67X6jp5UXU/edit?usp=sharing)

## License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)

