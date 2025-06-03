package com.rentcar.dao.impl;

import com.rentcar.dao.ClienteDAO;
import com.rentcar.model.Cliente;
import com.rentcar.util.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {
    private static final Logger logger = LogManager.getLogger(ClienteDAOImpl.class);

    @Override
    public Cliente create(Cliente cliente) throws SQLException {
        String SQL = "INSERT INTO Clientes (nombre, apellido, dni, numero_licencia, telefono) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getDni());
            pstmt.setString(4, cliente.getNumeroLicencia());
            pstmt.setString(5, cliente.getTelefono());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Creación de cliente fallida, no se afectaron filas.");
                throw new SQLException("Creación de cliente fallida, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                    logger.info("Cliente creado con ID: " + cliente.getId() + " - " + cliente.getNombre() + " " + cliente.getApellido());
                    return cliente;
                } else {
                    logger.error("Creación de cliente fallida, no se obtuvo ID.");
                    throw new SQLException("Creación de cliente fallida, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al crear cliente: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Cliente getById(int id) throws SQLException {
        String SQL = "SELECT id, nombre, apellido, dni, numero_licencia, telefono FROM Clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("dni"),
                            rs.getString("numero_licencia"),
                            rs.getString("telefono")
                    );
                    logger.debug("Cliente encontrado por ID " + id + ": " + cliente.getNombre());
                    return cliente;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener cliente por ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
        logger.debug("Cliente con ID " + id + " no encontrado.");
        return null;
    }

    @Override
    public List<Cliente> getAll() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String SQL = "SELECT id, nombre, apellido, dni, numero_licencia, telefono FROM Clientes";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("numero_licencia"),
                        rs.getString("telefono")
                );
                clientes.add(cliente);
            }
            logger.info("Listados " + clientes.size() + " clientes.");
            return clientes;
        } catch (SQLException e) {
            logger.error("Error al obtener todos los clientes: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean update(Cliente cliente) throws SQLException {
        String SQL = "UPDATE Clientes SET nombre = ?, apellido = ?, dni = ?, numero_licencia = ?, telefono = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getDni());
            pstmt.setString(4, cliente.getNumeroLicencia());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setInt(6, cliente.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Cliente actualizado con ID: " + cliente.getId());
                return true;
            } else {
                logger.warn("No se encontró cliente con ID " + cliente.getId() + " para actualizar.");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar cliente con ID " + cliente.getId() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String SQL = "DELETE FROM Clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Cliente eliminado con ID: " + id);
                return true;
            } else {
                logger.warn("No se encontró cliente con ID " + id + " para eliminar.");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar cliente con ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
    }
}