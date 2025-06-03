package com.rentcar.dao.impl;

import com.rentcar.dao.AlquilerDAO;
import com.rentcar.dao.VehiculoDAO; // Necesitamos el DAO de Vehiculo para actualizar el estado
import com.rentcar.dao.ClienteDAO; // Necesitamos el DAO de Cliente para verificar su existencia
import com.rentcar.model.Alquiler;
import com.rentcar.model.Vehiculo;
import com.rentcar.model.Cliente;
import com.rentcar.util.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAOImpl implements AlquilerDAO {
    private static final Logger logger = LogManager.getLogger(AlquilerDAOImpl.class);
    private VehiculoDAO vehiculoDAO;
    private ClienteDAO clienteDAO; // Añadir referencia al ClienteDAO

    public AlquilerDAOImpl() {
        this.vehiculoDAO = new VehiculoDAOImpl();
        this.clienteDAO = new ClienteDAOImpl(); // Instanciar ClienteDAO
    }

    @Override
    public Alquiler create(Alquiler alquiler) throws SQLException {
        throw new UnsupportedOperationException("El método 'create' para Alquiler no está implementado directamente. Utilice 'registrarAlquiler()' para gestionar alquileres y su impacto en la disponibilidad de vehículos.");
    }

    @Override
    public Alquiler getById(int id) throws SQLException {
        String SQL = "SELECT id, id_vehiculo, id_cliente, fecha_inicio, fecha_fin FROM Alquileres WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Alquiler alquiler = new Alquiler(
                            rs.getInt("id"),
                            rs.getInt("id_vehiculo"),
                            rs.getInt("id_cliente"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null
                    );
                    logger.debug("Alquiler encontrado por ID " + id + ": " + alquiler.getId());
                    return alquiler;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener alquiler por ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
        logger.debug("Alquiler con ID " + id + " no encontrado.");
        return null;
    }

    @Override
    public List<Alquiler> getAll() throws SQLException {
        List<Alquiler> alquileres = new ArrayList<>();
        String SQL = "SELECT id, id_vehiculo, id_cliente, fecha_inicio, fecha_fin FROM Alquileres";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Alquiler alquiler = new Alquiler(
                        rs.getInt("id"),
                        rs.getInt("id_vehiculo"),
                        rs.getInt("id_cliente"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null
                );
                alquileres.add(alquiler);
            }
            logger.info("Listados " + alquileres.size() + " alquileres.");
            return alquileres;
        } catch (SQLException e) {
            logger.error("Error al obtener todos los alquileres: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean update(Alquiler alquiler) throws SQLException {
        String SQL = "UPDATE Alquileres SET id_vehiculo = ?, id_cliente = ?, fecha_inicio = ?, fecha_fin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, alquiler.getIdVehiculo());
            pstmt.setInt(2, alquiler.getIdCliente());
            pstmt.setDate(3, Date.valueOf(alquiler.getFechaInicio()));
            pstmt.setDate(4, alquiler.getFechaFin() != null ? Date.valueOf(alquiler.getFechaFin()) : null);
            pstmt.setInt(5, alquiler.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Alquiler actualizado con ID: " + alquiler.getId());
                return true;
            } else {
                logger.warn("No se encontró alquiler con ID " + alquiler.getId() + " para actualizar.");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar alquiler con ID " + alquiler.getId() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            Alquiler alquilerAEliminar = getById(id);
            if (alquilerAEliminar == null) {
                logger.warn("No se encontró alquiler con ID " + id + " para eliminar.");
                return false;
            }

            String deleteAlquilerSQL = "DELETE FROM Alquileres WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAlquilerSQL)) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Error al eliminar el registro de alquiler.");
                }
                logger.info("Alquiler eliminado de la tabla Alquileres: ID " + id);
            }

            // Si el vehículo NO ha sido devuelto (fecha_fin es NULL), marcarlo como disponible
            if (alquilerAEliminar.getFechaFin() == null) {
                Vehiculo vehiculo = vehiculoDAO.getById(alquilerAEliminar.getIdVehiculo());
                if (vehiculo != null) {
                    String updateVehiculoSQL = "UPDATE Vehiculos SET disponible = ? WHERE id = ?";
                    try (PreparedStatement pstmtVehiculo = conn.prepareStatement(updateVehiculoSQL)) {
                        pstmtVehiculo.setBoolean(1, true);
                        pstmtVehiculo.setInt(2, vehiculo.getId());
                        int affectedRowsVehiculo = pstmtVehiculo.executeUpdate();
                        if (affectedRowsVehiculo == 0) {
                            throw new SQLException("Error al actualizar la disponibilidad del vehículo tras eliminar alquiler.");
                        }
                        logger.info("Vehículo ID " + vehiculo.getId() + " marcado como DISPONIBLE tras eliminación de alquiler.");
                    }
                } else {
                    logger.warn("Vehículo asociado al alquiler ID " + id + " no encontrado durante la eliminación del alquiler.");
                }
            } else {
                logger.info("El alquiler ID " + id + " ya había sido finalizado, no se alteró el estado del vehículo.");
            }

            conn.commit();
            logger.info("Transacción de eliminación de alquiler completada con éxito.");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Rollback de transacción de eliminación de alquiler debido a un error.");
                } catch (SQLException rollbackEx) {
                    logger.error("Error durante el rollback de eliminación de alquiler: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            logger.error("Error al eliminar alquiler (transacción revertida): " + e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error al cerrar conexión después de transacción de eliminación de alquiler: " + closeEx.getMessage(), closeEx);
                }
            }
        }
    }

    @Override
    public boolean registrarAlquiler(int idVehiculo, int idCliente, LocalDate fechaInicio) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Verificar si el vehículo existe y está disponible
            Vehiculo vehiculo = vehiculoDAO.getById(idVehiculo);
            if (vehiculo == null) {
                logger.warn("Intento de alquiler fallido: Vehículo con ID " + idVehiculo + " no encontrado.");
                throw new SQLException("El vehículo no existe.");
            }
            if (!vehiculo.isDisponible()) {
                logger.warn("Intento de alquiler fallido: Vehículo con ID " + idVehiculo + " no está disponible.");
                throw new SQLException("El vehículo no está disponible para alquiler.");
            }

            // 2. Verificar si el cliente existe
            Cliente cliente = clienteDAO.getById(idCliente);
            if (cliente == null) {
                logger.warn("Intento de alquiler fallido: Cliente con ID " + idCliente + " no encontrado.");
                throw new SQLException("El cliente no existe.");
            }

            // 3. Insertar el alquiler en la tabla Alquileres
            String insertAlquilerSQL = "INSERT INTO Alquileres (id_vehiculo, id_cliente, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertAlquilerSQL)) {
                pstmt.setInt(1, idVehiculo);
                pstmt.setInt(2, idCliente);
                pstmt.setDate(3, Date.valueOf(fechaInicio));
                pstmt.setDate(4, null); // fecha_fin es null inicialmente

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Error al insertar el registro de alquiler.");
                }
                logger.info("Alquiler registrado para Vehículo ID " + idVehiculo + " y Cliente ID " + idCliente);
            }

            // 4. Actualizar el estado del vehículo a no disponible
            String updateVehiculoSQL = "UPDATE Vehiculos SET disponible = ? WHERE id = ?";
            try (PreparedStatement pstmtVehiculo = conn.prepareStatement(updateVehiculoSQL)) {
                pstmtVehiculo.setBoolean(1, false);
                pstmtVehiculo.setInt(2, vehiculo.getId());
                int affectedRowsVehiculo = pstmtVehiculo.executeUpdate();
                if (affectedRowsVehiculo == 0) {
                    throw new SQLException("Error al actualizar la disponibilidad del vehículo.");
                }
                logger.info("Estado del Vehículo ID " + idVehiculo + " actualizado a NO DISPONIBLE.");
            }

            conn.commit();
            logger.info("Transacción de registro de alquiler completada con éxito.");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Rollback de transacción de alquiler debido a un error.");
                } catch (SQLException rollbackEx) {
                    logger.error("Error durante el rollback: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            logger.error("Error al registrar alquiler (transacción revertida): " + e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error al cerrar conexión después de transacción: " + closeEx.getMessage(), closeEx);
                }
            }
        }
    }

    @Override
    public boolean registrarDevolucion(int idAlquiler) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Alquiler alquiler = getById(idAlquiler);
            if (alquiler == null) {
                logger.warn("Intento de devolución fallido: Alquiler con ID " + idAlquiler + " no encontrado.");
                throw new SQLException("El alquiler no existe.");
            }
            if (alquiler.getFechaFin() != null) {
                logger.warn("Intento de devolución fallido: Alquiler con ID " + idAlquiler + " ya ha sido finalizado.");
                throw new SQLException("Este alquiler ya ha sido finalizado.");
            }

            String updateAlquilerSQL = "UPDATE Alquileres SET fecha_fin = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateAlquilerSQL)) {
                pstmt.setDate(1, Date.valueOf(LocalDate.now()));
                pstmt.setInt(2, idAlquiler);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Error al actualizar la fecha de finalización del alquiler.");
                }
                logger.info("Fecha de finalización registrada para Alquiler ID " + idAlquiler);
            }

            Vehiculo vehiculo = vehiculoDAO.getById(alquiler.getIdVehiculo());
            if (vehiculo == null) {
                logger.error("Error crítico: Vehículo asociado al alquiler ID " + idAlquiler + " no encontrado.");
                throw new SQLException("Vehículo asociado no encontrado, no se puede actualizar su estado.");
            }
            vehiculo.setDisponible(true);

            String updateVehiculoSQL = "UPDATE Vehiculos SET disponible = ? WHERE id = ?";
            try (PreparedStatement pstmtVehiculo = conn.prepareStatement(updateVehiculoSQL)) {
                pstmtVehiculo.setBoolean(1, true);
                pstmtVehiculo.setInt(2, vehiculo.getId());
                int affectedRowsVehiculo = pstmtVehiculo.executeUpdate();
                if (affectedRowsVehiculo == 0) {
                    throw new SQLException("Error al actualizar la disponibilidad del vehículo.");
                }
                logger.info("Estado del Vehículo ID " + vehiculo.getId() + " actualizado a DISPONIBLE.");
            }

            conn.commit();
            logger.info("Transacción de registro de devolución completada con éxito.");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Rollback de transacción de devolución debido a un error.");
                } catch (SQLException rollbackEx) {
                    logger.error("Error durante el rollback: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            logger.error("Error al registrar devolución (transacción revertida): " + e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error al cerrar conexión después de transacción: " + closeEx.getMessage(), closeEx);
                }
            }
        }
    }

    @Override
    public List<Alquiler> getAlquileresActivos() throws SQLException {
        List<Alquiler> alquileres = new ArrayList<>();
        String SQL = "SELECT id, id_vehiculo, id_cliente, fecha_inicio, fecha_fin FROM Alquileres WHERE fecha_fin IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Alquiler alquiler = new Alquiler(
                        rs.getInt("id"),
                        rs.getInt("id_vehiculo"),
                        rs.getInt("id_cliente"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        null // La fecha de fin es NULL para alquileres activos
                );
                alquileres.add(alquiler);
            }
            logger.info("Listados " + alquileres.size() + " alquileres activos.");
            return alquileres;
        } catch (SQLException e) {
            logger.error("Error al obtener alquileres activos: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Alquiler> getAlquileresByClienteId(int clienteId) throws SQLException {
        List<Alquiler> alquileres = new ArrayList<>();
        String SQL = "SELECT id, id_vehiculo, id_cliente, fecha_inicio, fecha_fin FROM Alquileres WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, clienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Alquiler alquiler = new Alquiler(
                            rs.getInt("id"),
                            rs.getInt("id_vehiculo"),
                            rs.getInt("id_cliente"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null
                    );
                    alquileres.add(alquiler);
                }
            }
            logger.info("Listados " + alquileres.size() + " alquileres para el Cliente ID " + clienteId);
            return alquileres;
        } catch (SQLException e) {
            logger.error("Error al obtener alquileres por Cliente ID " + clienteId + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Alquiler> getAlquileresByVehiculoId(int vehiculoId) throws SQLException {
        List<Alquiler> alquileres = new ArrayList<>();
        String SQL = "SELECT id, id_vehiculo, id_cliente, fecha_inicio, fecha_fin FROM Alquileres WHERE id_vehiculo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, vehiculoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Alquiler alquiler = new Alquiler(
                            rs.getInt("id"),
                            rs.getInt("id_vehiculo"),
                            rs.getInt("id_cliente"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null
                    );
                    alquileres.add(alquiler);
                }
            }
            logger.info("Listados " + alquileres.size() + " alquileres para el Vehículo ID " + vehiculoId);
            return alquileres;
        } catch (SQLException e) {
            logger.error("Error al obtener alquileres por Vehículo ID " + vehiculoId + ": " + e.getMessage(), e);
            throw e;
        }
    }
}