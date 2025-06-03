package main;

import com.rentcar.dao.VehiculoDAO;
import com.rentcar.dao.ClienteDAO;
import com.rentcar.dao.AlquilerDAO;
import com.rentcar.dao.impl.VehiculoDAOImpl;
import com.rentcar.dao.impl.ClienteDAOImpl;
import com.rentcar.dao.impl.AlquilerDAOImpl;
import com.rentcar.model.Vehiculo;
import com.rentcar.model.Cliente;
import com.rentcar.model.Alquiler;
import com.rentcar.util.DatabaseConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RentCarApp {
    private static final Logger logger = LogManager.getLogger(RentCarApp.class);
    private static final Scanner scanner = new Scanner(System.in);

    private static VehiculoDAO vehiculoDAO;
    private static ClienteDAO clienteDAO;
    private static AlquilerDAO alquilerDAO;

    public static void main(String[] args) {
        // 1. Inicializar la base de datos (crear tablas si no existen)
        DatabaseConnection.createTables();

        // 2. Instanciar los DAOs
        vehiculoDAO = new VehiculoDAOImpl();
        clienteDAO = new ClienteDAOImpl();
        alquilerDAO = new AlquilerDAOImpl();

        // 3. Menú principal de la aplicación
        int opcionPrincipal = 0;
        do {
            mostrarMenuPrincipal();
            opcionPrincipal = obtenerOpcionMenu(1, 4);
            ejecutarOpcionPrincipal(opcionPrincipal);
        } while (opcionPrincipal != 4); //

        scanner.close();
        logger.info("Aplicación de Rent-A-Car finalizada.");
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- Sistema de Gestión de Rent-A-Car ---");
        System.out.println("1. Gestión de Vehículos");
        System.out.println("2. Gestión de Clientes");
        System.out.println("3. Gestión de Alquileres");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void ejecutarOpcionPrincipal(int opcion) {
        switch (opcion) {
            case 1:
                menuGestionVehiculos();
                break;
            case 2:
                menuGestionClientes();
                break;
            case 3:
                menuGestionAlquileres();
                break;
            case 4:
                System.out.println("Saliendo de la aplicación...");
                break;
            default:
                System.out.println("Opción inválida. Intente de nuevo.");
        }
    }

    // --- Métodos de Gestión de Vehículos ---
    private static void menuGestionVehiculos() {
        int opcion = 0;
        do { // <-- do-while loop start
            System.out.println("\n--- Gestión de Vehículos ---");
            System.out.println("1. Registrar Vehículo");
            System.out.println("2. Listar Todos los Vehículos");
            System.out.println("3. Buscar Vehículo por ID");
            System.out.println("4. Actualizar Vehículo");
            System.out.println("5. Eliminar Vehículo");
            System.out.println("6. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = obtenerOpcionMenu(1, 6);

            try {
                switch (opcion) {
                    case 1: registrarVehiculo(); break;
                    case 2: listarVehiculos(); break;
                    case 3: buscarVehiculoPorId(); break;
                    case 4: actualizarVehiculo(); break;
                    case 5: eliminarVehiculo(); break;
                    case 6: System.out.println("Volviendo al menú principal..."); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                logger.error("Error de base de datos en gestión de vehículos: " + e.getMessage(), e);
                System.err.println("Ocurrió un error de base de datos: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Error inesperado en gestión de vehículos: " + e.getMessage(), e);
                System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (opcion != 6); // <-- do-while loop end
    }

    private static void registrarVehiculo() throws SQLException {
        System.out.println("\n--- Registrar Nuevo Vehículo ---");
        System.out.print("Marca: ");
        String marca = scanner.nextLine().trim();
        if (marca.isEmpty()) { System.out.println("La marca no puede estar vacía."); return; }

        System.out.print("Modelo: ");
        String modelo = scanner.nextLine().trim();
        if (modelo.isEmpty()) { System.out.println("El modelo no puede estar vacío."); return; }

        System.out.print("Año: ");
        int anio = obtenerEnteroValido();
        if (anio == -1) { System.out.println("Entrada de año inválida. Operación cancelada."); return; }

        System.out.print("Patente: ");
        String patente = scanner.nextLine().trim();
        if (patente.isEmpty()) { System.out.println("La patente no puede estar vacía."); return; }

        Vehiculo nuevoVehiculo = new Vehiculo(marca, modelo, anio, patente, true); // Por defecto disponible
        vehiculoDAO.create(nuevoVehiculo);
        System.out.println("Vehículo registrado con éxito: " + nuevoVehiculo);
    }

    private static void listarVehiculos() throws SQLException {
        System.out.println("\n--- Listado de Vehículos ---");
        List<Vehiculo> vehiculos = vehiculoDAO.getAll();
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehículos registrados.");
        } else {
            for (Vehiculo vehiculo : vehiculos) {
                System.out.println(vehiculo);
            }
        }
    }

    private static void buscarVehiculoPorId() throws SQLException {
        System.out.print("Ingrese el ID del vehículo a buscar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID inválida. Operación cancelada."); return; }

        Vehiculo vehiculo = vehiculoDAO.getById(id);
        if (vehiculo != null) {
            System.out.println("Vehículo encontrado: " + vehiculo);
        } else {
            System.out.println("Vehículo con ID " + id + " no encontrado.");
        }
    }

    private static void actualizarVehiculo() throws SQLException {
        System.out.print("Ingrese el ID del vehículo a actualizar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID inválida. Operación cancelada."); return; }

        Vehiculo vehiculoExistente = vehiculoDAO.getById(id);
        if (vehiculoExistente == null) {
            System.out.println("Vehículo con ID " + id + " no encontrado.");
            return;
        }

        System.out.println("Datos actuales del vehículo: " + vehiculoExistente);
        System.out.println("Ingrese nuevos datos (deje en blanco para mantener el actual):");

        System.out.print("Nueva Marca (" + vehiculoExistente.getMarca() + "): ");
        String marca = scanner.nextLine();
        if (!marca.isEmpty()) vehiculoExistente.setMarca(marca);

        System.out.print("Nuevo Modelo (" + vehiculoExistente.getModelo() + "): ");
        String modelo = scanner.nextLine();
        if (!modelo.isEmpty()) vehiculoExistente.setModelo(modelo);

        System.out.print("Nuevo Año (" + vehiculoExistente.getAnio() + "): ");
        String anioStr = scanner.nextLine();
        if (!anioStr.isEmpty()) {
            try {
                vehiculoExistente.setAnio(Integer.parseInt(anioStr));
            } catch (NumberFormatException e) {
                System.out.println("Año inválido. Se mantendrá el actual.");
                logger.warn("Intento de actualización de año con formato inválido: " + anioStr);
            }
        }

        System.out.print("Nueva Patente (" + vehiculoExistente.getPatente() + "): ");
        String patente = scanner.nextLine();
        if (!patente.isEmpty()) vehiculoExistente.setPatente(patente);

        // La disponibilidad se maneja principalmente a través de alquileres/devoluciones
        // Pero podríamos permitir una actualización manual si es necesario
        System.out.print("Disponible (true/false) (" + vehiculoExistente.isDisponible() + "): ");
        String disponibleStr = scanner.nextLine();
        if (!disponibleStr.isEmpty()) {
            if (disponibleStr.equalsIgnoreCase("true")) {
                vehiculoExistente.setDisponible(true);
            } else if (disponibleStr.equalsIgnoreCase("false")) {
                vehiculoExistente.setDisponible(false);
            } else {
                System.out.println("Valor inválido para 'disponible'. Se mantendrá el actual.");
                logger.warn("Intento de actualización de disponible con valor inválido: " + disponibleStr);
            }
        }


        if (vehiculoDAO.update(vehiculoExistente)) {
            System.out.println("Vehículo actualizado con éxito.");
        } else {
            System.out.println("No se pudo actualizar el vehículo.");
        }
    }

    private static void eliminarVehiculo() throws SQLException {
        System.out.print("Ingrese el ID del vehículo a eliminar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID inválida. Operación cancelada."); return; }

        // Antes de eliminar un vehículo, verificar si tiene alquileres activos.
        List<Alquiler> alquileresAsociados = alquilerDAO.getAlquileresByVehiculoId(id);
        if (alquileresAsociados != null && !alquileresAsociados.isEmpty()) {
            System.out.println("ERROR: No se puede eliminar el vehículo con ID " + id + " porque tiene alquileres asociados. Finalice los alquileres primero.");
            return;
        }

        if (vehiculoDAO.delete(id)) {
            System.out.println("Vehículo eliminado con éxito.");
        } else {
            System.out.println("No se encontró vehículo con ID " + id + " para eliminar.");
        }
    }

    // --- Métodos de Gestión de Clientes ---
    private static void menuGestionClientes() {
        int opcion = 0;
        do { // <-- do-while loop start
            System.out.println("\n--- Gestión de Clientes ---");
            System.out.println("1. Registrar Cliente");
            System.out.println("2. Listar Todos los Clientes");
            System.out.println("3. Buscar Cliente por ID");
            System.out.println("4. Actualizar Cliente");
            System.out.println("5. Eliminar Cliente");
            System.out.println("6. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = obtenerOpcionMenu(1, 6);

            try {
                switch (opcion) {
                    case 1: registrarCliente(); break;
                    case 2: listarClientes(); break;
                    case 3: buscarClientePorId(); break;
                    case 4: actualizarCliente(); break;
                    case 5: eliminarCliente(); break;
                    case 6: System.out.println("Volviendo al menú principal..."); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                logger.error("Error de base de datos en gestión de clientes: " + e.getMessage(), e);
                System.err.println("Ocurrió un error de base de datos: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Error inesperado en gestión de clientes: " + e.getMessage(), e);
                System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (opcion != 6); //
    }

    private static void registrarCliente() throws SQLException {
        System.out.println("\n--- Registrar Nuevo Cliente ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("El nombre no puede estar vacía."); return; }

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        if (apellido.isEmpty()) { System.out.println("El apellido no puede estar vacío."); return; }

        System.out.print("DNI: ");
        String dni = scanner.nextLine().trim();

        System.out.print("Número de Licencia: ");
        String numeroLicencia = scanner.nextLine().trim();

        System.out.print("Número de Teléfono: ");
        String telefono = scanner.nextLine().trim();

        Cliente nuevoCliente = new Cliente(nombre, apellido, dni, numeroLicencia, telefono);
        clienteDAO.create(nuevoCliente);
        System.out.println("Cliente registrado con éxito: " + nuevoCliente);
    }

    private static void listarClientes() throws SQLException {
        System.out.println("\n--- Listado de Clientes ---");
        List<Cliente> clientes = clienteDAO.getAll();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
        } else {
            for (Cliente cliente : clientes) {
                System.out.println(cliente);
            }
        }
    }

    private static void buscarClientePorId() throws SQLException {
        System.out.print("Ingrese el ID del cliente a buscar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID inválida. Operación cancelada."); return; }

        Cliente cliente = clienteDAO.getById(id);
        if (cliente != null) {
            System.out.println("Cliente encontrado: " + cliente);
        } else {
            System.out.println("Cliente con ID " + id + " no encontrado.");
        }
    }

    private static void actualizarCliente() throws SQLException {
        System.out.print("Ingrese el ID del cliente a actualizar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID inválida. Operación cancelada."); return; }

        Cliente clienteExistente = clienteDAO.getById(id);
        if (clienteExistente == null) {
            System.out.println("Cliente con ID " + id + " no encontrado.");
            return;
        }

        System.out.println("Datos actuales del cliente: " + clienteExistente);
        System.out.println("Ingrese nuevos datos (deje en blanco para mantener el actual):");

        System.out.print("Nuevo Nombre (" + clienteExistente.getNombre() + "): ");
        String nombre = scanner.nextLine();
        if (!nombre.isEmpty()) clienteExistente.setNombre(nombre);

        System.out.print("Nuevo Apellido (" + clienteExistente.getApellido() + "): ");
        String apellido = scanner.nextLine();
        if (!apellido.isEmpty()) clienteExistente.setApellido(apellido);

        System.out.print("Nuevo DNI (" + clienteExistente.getDni() + "): ");
        String dni = scanner.nextLine();
        if (!dni.isEmpty()) clienteExistente.setDni(dni);

        System.out.print("Nuevo Número de Licencia (" + clienteExistente.getNumeroLicencia() + "): ");
        String numeroLicencia = scanner.nextLine();
        if (!numeroLicencia.isEmpty()) clienteExistente.setNumeroLicencia(numeroLicencia);

        System.out.print("Nuevo Número de Teléfono (" + clienteExistente.getTelefono() + "): ");
        String telefono = scanner.nextLine();
        if (!telefono.isEmpty()) clienteExistente.setTelefono(telefono);

        if (clienteDAO.update(clienteExistente)) {
            System.out.println("Cliente actualizado con éxito.");
        } else {
            System.out.println("No se pudo actualizar el cliente.");
        }
    }

    private static void eliminarCliente() throws SQLException {
        System.out.print("Ingrese el ID del cliente a eliminar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID inválida. Operación cancelada."); return; }

        // Antes de eliminar un cliente, verificar si tiene alquileres activos.
        List<Alquiler> alquileresActivos = alquilerDAO.getAlquileresByClienteId(id);
        if (alquileresActivos != null && !alquileresActivos.isEmpty()) {
            System.out.println("ERROR: No se puede eliminar el cliente con ID " + id + " porque tiene alquileres activos. Finalice los alquileres primero.");
            return;
        }

        if (clienteDAO.delete(id)) {
            System.out.println("Cliente eliminado con éxito.");
        } else {
            System.out.println("No se encontró cliente con ID " + id + " para eliminar.");
        }
    }

    // --- Métodos de Gestión de Alquileres ---
    private static void menuGestionAlquileres() {
        int opcion = 0;
        do { // <-- do-while loop start
            System.out.println("\n--- Gestión de Alquileres ---");
            System.out.println("1. Registrar Nuevo Alquiler");
            System.out.println("2. Registrar Devolución");
            System.out.println("3. Listar Alquileres Activos");
            System.out.println("4. Listar Todos los Alquileres");
            System.out.println("5. Buscar Alquiler por ID");
            System.out.println("6. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = obtenerOpcionMenu(1, 6);

            try {
                switch (opcion) {
                    case 1: registrarNuevoAlquiler(); break;
                    case 2: registrarDevolucion(); break;
                    case 3: listarAlquileresActivos(); break;
                    case 4: listarTodosAlquileres(); break;
                    case 5: buscarAlquilerPorId(); break;
                    case 6: System.out.println("Volviendo al menú principal..."); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                logger.error("Error de base de datos en gestión de alquileres: " + e.getMessage(), e);
                System.err.println("Ocurrió un error de base de datos: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Error inesperado en gestión de alquileres: " + e.getMessage(), e);
                System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (opcion != 6); // <-- do-while loop end
    }

    private static void registrarNuevoAlquiler() throws SQLException {
        System.out.println("\n--- Registrar Nuevo Alquiler ---");
        System.out.print("Ingrese ID del Vehículo: ");
        int idVehiculo = obtenerEnteroValido();
        if (idVehiculo == -1) { System.out.println("Entrada de ID de vehículo inválida. Operación cancelada."); return; }

        System.out.print("Ingrese ID del Cliente: ");
        int idCliente = obtenerEnteroValido();
        if (idCliente == -1) { System.out.println("Entrada de ID de cliente inválida. Operación cancelada."); return; }

        LocalDate fechaInicio = LocalDate.now();
        System.out.println("Fecha de inicio de alquiler: " + fechaInicio + " (automático)");

        try {
            // Verificar si el vehículo y cliente existen y el vehículo está disponible
            Vehiculo vehiculo = vehiculoDAO.getById(idVehiculo);
            if (vehiculo == null) {
                System.out.println("Error: Vehículo con ID " + idVehiculo + " no encontrado.");
                return;
            }
            if (!vehiculo.isDisponible()) {
                System.out.println("Error: El vehículo '" + vehiculo.getMarca() + " " + vehiculo.getModelo() + "' (ID: " + idVehiculo + ") no está disponible para alquiler.");
                return;
            }

            Cliente cliente = clienteDAO.getById(idCliente);
            if (cliente == null) {
                System.out.println("Error: Cliente con ID " + idCliente + " no encontrado.");
                return;
            }

            if (alquilerDAO.registrarAlquiler(idVehiculo, idCliente, fechaInicio)) {
                System.out.println("Alquiler registrado con éxito.");
            } else {
                System.out.println("No se pudo registrar el alquiler. Verifique el vehículo y el cliente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar alquiler: " + e.getMessage());
            logger.error("Error al registrar alquiler: " + e.getMessage(), e);
        }
    }

    private static void registrarDevolucion() throws SQLException {
        System.out.println("\n--- Registrar Devolución ---");
        System.out.print("Ingrese el ID del Alquiler a devolver: ");
        int idAlquiler = obtenerEnteroValido();
        if (idAlquiler == -1) { System.out.println("Entrada de ID de alquiler inválida. Operación cancelada."); return; }

        try {
            if (alquilerDAO.registrarDevolucion(idAlquiler)) {
                System.out.println("Devolución registrada con éxito.");
            } else {
                System.out.println("No se pudo registrar la devolución. Verifique el ID del alquiler.");
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar devolución: " + e.getMessage());
            logger.error("Error al registrar devolución: " + e.getMessage(), e);
        }
    }

    private static void listarAlquileresActivos() throws SQLException {
        System.out.println("\n--- Listado de Alquileres Activos ---");
        List<Alquiler> alquileres = alquilerDAO.getAlquileresActivos();
        if (alquileres.isEmpty()) {
            System.out.println("No hay alquileres activos.");
        } else {
            for (Alquiler alquiler : alquileres) {
                System.out.println(alquiler);
            }
        }
    }

    private static void listarTodosAlquileres() throws SQLException {
        System.out.println("\n--- Listado de Todos los Alquileres ---");
        List<Alquiler> alquileres = alquilerDAO.getAll();
        if (alquileres.isEmpty()) {
            System.out.println("No hay alquileres registrados.");
        } else {
            for (Alquiler alquiler : alquileres) {
                System.out.println(alquiler);
            }
        }
    }

    private static void buscarAlquilerPorId() throws SQLException {
        System.out.print("Ingrese el ID del alquiler a buscar: ");
        int id = obtenerEnteroValido();
        if (id == -1) { System.out.println("Entrada de ID de alquiler inválida. Operación cancelada."); return; }

        Alquiler alquiler = alquilerDAO.getById(id);
        if (alquiler != null) {
            System.out.println("Alquiler encontrado: " + alquiler);
        } else {
            System.out.println("Alquiler con ID " + id + " no encontrado.");
        }
    }

    // --- Métodos de Utilidad para la Entrada del Usuario (sin cambios, solo se mantiene) ---
    private static int obtenerOpcionMenu(int min, int max) {
        int opcion = -1;
        boolean entradaValida = false;
        while (!entradaValida) {
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
                if (opcion >= min && opcion <= max) {
                    entradaValida = true;
                } else {
                    System.out.println("Opción fuera de rango. Por favor, ingrese un número entre " + min + " y " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine();
            }
        }
        return opcion;
    }

    private static int obtenerEnteroValido() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
                scanner.nextLine();
                return -1; // Retorna -1 para indicar un error de entrada
            }
        }
    }

    private static LocalDate obtenerFechaValida() {
        while (true) {
            System.out.print("Ingrese fecha (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine().trim();
            if (fechaStr.isEmpty()) {
                System.out.println("La fecha no puede estar vacía.");
                continue;
            }
            try {
                return LocalDate.parse(fechaStr);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
        }
    }
}