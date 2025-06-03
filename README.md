README.txt
Proyecto: Sistema de Gestión de Alquiler de Vehículos (Rent-A-Car)

===================================================================
1. Descripción General del Proyecto
===================================================================
Este proyecto implementa un sistema básico de gestión para una empresa de alquiler de vehículos. Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre vehículos, clientes y alquileres. La aplicación se ejecuta en consola y utiliza una base de datos embebida H2 para la persistencia de datos.

Características principales:
- Gestión de Vehículos: Registro, listado, búsqueda, actualización y eliminación de vehículos.
- Gestión de Clientes: Registro, listado, búsqueda, actualización y eliminación de clientes.
- Gestión de Alquileres: Registro de nuevos alquileres, registro de devoluciones, y listado de alquileres activos e históricos.
- Persistencia de Datos: Los datos se almacenan en una base de datos H2. La creación de tablas se maneja automáticamente al iniciar la aplicación.
- Logging: Utiliza Log4j2 para el registro de eventos y errores, lo que facilita la depuración.

===================================================================
2. Estructura del Proyecto
===================================================================
El proyecto sigue una estructura Maven estándar y una arquitectura simple para la gestión de datos:

- `src/main/java/com/rentcar/main/RentCarApp.java`: Clase principal que contiene el método `main` y la lógica de la interfaz de usuario en consola.
- `src/main/java/com/rentcar/model/`: Contiene las clases POJO (Plain Old Java Object) que representan las entidades del negocio (Vehiculo, Cliente, Alquiler).
- `src/main/java/com/rentcar/dao/`: Define las interfaces DAO (Data Access Object) para cada entidad, estableciendo el contrato para las operaciones de persistencia.
- `src/main/java/com/rentcar/dao/impl/`: Contiene las implementaciones concretas de las interfaces DAO, utilizando JDBC para interactuar con la base de datos.
- `src/main/java/com/rentcar/util/DatabaseConnection.java`: Clase de utilidad para gestionar la conexión a la base de datos H2 y la creación inicial de las tablas.
- `src/main/resources/log4j2.xml`: Archivo de configuración para Log4j2.
- `pom.xml`: Archivo de configuración de Maven, que gestiona las dependencias del proyecto (H2 Database, Log4j2) y el ciclo de vida de construcción.

===================================================================
3. Requisitos del Sistema
===================================================================
- Java Development Kit (JDK) 17 o superior.
- Apache Maven (preferiblemente para la construcción y gestión de dependencias).
- Un IDE como IntelliJ IDEA, Eclipse o VS Code con soporte para Java y Maven (opcional, pero recomendado para facilidad de uso).

===================================================================
4. Cómo Ejecutar el Código
===================================================================

Este proyecto utiliza Maven para la gestión de dependencias y la construcción.

**Opción A: Ejecutar desde un IDE (Recomendado)**

1.  **Importar el Proyecto:**
    * Abre tu IDE (IntelliJ IDEA, Eclipse, VS Code).
    * Selecciona "Importar Proyecto" o "Abrir Proyecto".
    * Navega hasta el directorio raíz de este proyecto (donde se encuentra el archivo `pom.xml`) y ábrelo. El IDE debería reconocerlo automáticamente como un proyecto Maven y descargar las dependencias.

2.  **Compilar y Ejecutar:**
    * Una vez que el proyecto esté importado y las dependencias resueltas (puede tomar unos segundos la primera vez), busca la clase `RentCarApp.java` dentro de `src/main/java/com/rentcar/main/`.
    * Haz clic derecho en `RentCarApp.java` y selecciona "Run 'RentCarApp.main()'" o una opción similar.

**Opción B: Ejecutar desde la Línea de Comandos (con Maven instalado)**

1.  **Navegar al Directorio del Proyecto:**
    * Abre una terminal o símbolo del sistema.
    * Navega hasta el directorio raíz del proyecto donde se encuentra el archivo `pom.xml`:
        ```bash
        cd /ruta/a/tu/proyecto/rentcar_app
        ```

2.  **Compilar el Proyecto:**
    * Ejecuta el siguiente comando Maven para compilar el código y descargar las dependencias:
        ```bash
        mvn clean install
        ```
        Esto generará un archivo `.jar` ejecutable en el directorio `target/`.

3.  **Ejecutar la Aplicación:**
    * Una vez que la compilación sea exitosa, ejecuta la aplicación usando el comando `java -jar`. El nombre del JAR dependerá de la configuración en `pom.xml`, pero generalmente sigue el formato `nombre-del-proyecto-version.jar`. Por ejemplo:
        ```bash
        java -jar target/rentcar-app-1.0-SNAPSHOT.jar
        ```
        (Ajusta `rentcar-app-1.0-SNAPSHOT.jar` al nombre real del JAR si es diferente).

    * Alternativamente, puedes usar el plugin exec de Maven para ejecutar directamente sin generar el JAR:
        ```bash
        mvn exec:java -Dexec.mainClass="com.rentcar.main.RentCarApp"
        ```

**Ubicación de la Base de Datos H2:**
Al ejecutar la aplicación, se creará un archivo de base de datos H2 llamado `rentcar_db.mv.db` (y posiblemente `rentcar_db.trace.db`) en el mismo directorio desde donde se ejecuta el comando (o en la raíz del proyecto si se ejecuta desde un IDE).

===================================================================
5. Qué Hace el Código al Ejecutar
===================================================================

Al iniciar la aplicación (`RentCarApp.java`), ocurre lo siguiente:

1.  **Inicialización de la Base de Datos:**
    * La primera acción en el método `main` es la llamada a `DatabaseConnection.createTables();`.
    * Este método se conecta a la base de datos H2 (`jdbc:h2:./rentcar_db`).
    * Ejecuta sentencias `CREATE TABLE IF NOT EXISTS` para `Vehiculos`, `Clientes` y `Alquileres`. Esto asegura que las tablas existan; si ya están creadas de una ejecución anterior, no se recrean y los datos existentes no se pierden.

2.  **Instanciación de DAOs:**
    * Se crean instancias de `VehiculoDAOImpl`, `ClienteDAOImpl` y `AlquilerDAOImpl`. Estas son las clases responsables de todas las interacciones con la base de datos para cada tipo de entidad.

3.  **Menú de Consola:**
    * La aplicación presenta un menú principal interactivo en la consola, permitiendo al usuario navegar entre la gestión de vehículos, clientes y alquileres.
    * Dentro de cada sección, se ofrecen opciones para crear, listar, buscar, actualizar y eliminar registros.

4.  **Operaciones con la Base de Datos:**
    * Cada vez que el usuario realiza una operación (ej. "Registrar Vehículo", "Registrar Devolución"), la lógica en `RentCarApp` invoca el método correspondiente en el DAO pertinente.
    * El DAO se encarga de abrir una conexión a la base de datos (a través de `DatabaseConnection.getConnection()`), ejecutar la sentencia SQL necesaria (INSERT, SELECT, UPDATE, DELETE), manejar los resultados y cerrar la conexión.

5.  **Persistencia:**
    * Todos los datos ingresados o modificados por el usuario se guardan directamente en el archivo de base de datos H2 (`rentcar_db.mv.db`). Esto significa que los datos persistirán incluso después de cerrar y volver a abrir la aplicación.

Este proyecto ofrece una demostración clara de una aplicación Java básica con persistencia de datos mediante JDBC y una base de datos embebida.

===================================================================
