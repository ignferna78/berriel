# Casa Berriel: desarrollo local

Aplicación Maven de dos módulos. El backend Spring Boot sirve las páginas
Thymeleaf y depende de los recursos del frontend.

## Entorno preparado en este Mac

- JDK 17: `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home`.
- Nivel de lenguaje y destino de compilación: Java 11.
- Maven 3.9.9 instalado. El wrapper del repositorio usa Maven 3.6.0.
- PostgreSQL 15: `/usr/local/opt/postgresql@15/bin`.
- Aplicación: http://localhost:8333/home/index.

## IntelliJ IDEA

1. Abre la carpeta del proyecto y recarga todos los proyectos desde la ventana Maven.
2. En **Project Structure → Project**, selecciona el SDK **17** y nivel **11**.
3. En **Settings → Build, Execution, Deployment → Build Tools → Maven**,
   usa `/usr/local/Cellar/maven/3.9.9/libexec` como Maven home.
   En **Importing** y **Runner**, selecciona el JDK del proyecto (17).
   Esto evita que Maven use el Java 23 de Homebrew.
4. En el terminal integrado ejecuta `bash scripts/local-db.sh start`.
5. Selecciona la configuración compartida **Berriel local** y pulsa Run o Debug.
   Si no aparece todavía, vuelve a abrir el proyecto; está en `.run/`.

La configuración ejecuta `com.project.casaberriel.App`, usa el módulo
`casaberriel-backend` y activa exclusivamente el perfil `local`.
No hay que arrancar otro servidor frontend ni compilar Elm para estas páginas.

## Alternativa desde el terminal

```sh
bash scripts/run-local.sh
```

Este comando inicia la base local, empaqueta ambos módulos y arranca la aplicación
con Java 17. Detén la aplicación con Ctrl+C. La base permanece activa y conserva
sus datos; para detenerla:

```sh
bash scripts/local-db.sh stop
```

Para consultar su estado: `bash scripts/local-db.sh status`.
Puedes cambiar las rutas de las herramientas mediante `BERRIEL_JAVA_HOME` y
`BERRIEL_PG_BIN`.

## Base y correo de desarrollo

La base `berriel_local` se crea vacía en `.local/postgres`, dentro del proyecto.
Escucha únicamente en `127.0.0.1:55432`, con usuario `berriel` y autenticación
local de confianza (sin contraseña). Cualquier proceso de este Mac puede acceder
a ella; es solo para datos de prueba. No utiliza ni modifica otra instalación
de datos PostgreSQL. Hibernate crea/actualiza las tablas al arrancar.

El perfil `local` utiliza el envío real de correo de `EmailConfig`, igual que los
demás perfiles. Los registros, reservas y recuperaciones de contraseña pueden
enviar mensajes reales. Se conserva la configuración original de correo;
`SPRING_MAIL_USERNAME` y `SPRING_MAIL_PASSWORD` permiten sustituir sus credenciales.

## Comprobaciones y límites

El arranque por script ejecuta las pruebas antes de empaquetar. Las pruebas del
controlador de reservas usan JUnit 5 y se ejecutan junto al resto de la suite.

Para ejecutar la suite explícitamente con el JDK indicado:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn test
```

El arranque requiere iniciar PostgreSQL desde un terminal que permita memoria
compartida. Si `initdb` muestra `shmget ... Operation not permitted` desde un
entorno restringido, ejecuta `bash scripts/local-db.sh start` en el terminal de
IntelliJ o Terminal de macOS.

Persisten los problemas de autorización y validación identificados en la revisión;
este perfil enlaza el servidor web únicamente a la interfaz local.
