package com.rentcar.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
    // Cambiado: Eliminado INIT=RUNSCRIPT del JDBC_URL
    private static final String JDBC_URL = "jdbc:h2:./rentcar_db"; // <-- ¡CAMBIO AQUÍ!
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            logger.fatal("H2 JDBC Driver no encontrado. Asegúrese de que la dependencia esté en el classpath.", e);
            throw new SQLException("H2 JDBC Driver no encontrado.", e);
        }
    }


    public static void createTables() {
        String createVehiculosTableSQL = """
            CREATE TABLE IF NOT EXISTS Vehiculos (
                id INT AUTO_INCREMENT PRIMARY KEY,
                marca VARCHAR(255) NOT NULL,
                modelo VARCHAR(255) NOT NULL,
                anio INT NOT NULL,
                patente VARCHAR(10) UNIQUE NOT NULL,
                disponible BOOLEAN DEFAULT TRUE
            );
            """;

        String createClientesTableSQL = """
            CREATE TABLE IF NOT EXISTS Clientes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(255) NOT NULL,
                apellido VARCHAR(255) NOT NULL,
                dni VARCHAR(20) UNIQUE,
                numero_licencia VARCHAR(50),
                telefono VARCHAR(20)
            );
            """;

        String createAlquileresTableSQL = """
            CREATE TABLE IF NOT EXISTS Alquileres (
                id INT AUTO_INCREMENT PRIMARY KEY,
                id_vehiculo INT NOT NULL,
                id_cliente INT NOT NULL,
                fecha_inicio DATE NOT NULL,
                fecha_fin DATE,
                FOREIGN KEY (id_vehiculo) REFERENCES Vehiculos(id) ON DELETE RESTRICT,
                FOREIGN KEY (id_cliente) REFERENCES Clientes(id) ON DELETE RESTRICT
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createVehiculosTableSQL);
            logger.info("Tabla 'Vehiculos' verificada/creada.");
            stmt.execute(createClientesTableSQL);
            logger.info("Tabla 'Clientes' verificada/creada.");
            stmt.execute(createAlquileresTableSQL);
            logger.info("Tabla 'Alquileres' verificada/creada.");
        } catch (SQLException e) {
            logger.error("Error al crear tablas en la base de datos: " + e.getMessage(), e);
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }
}