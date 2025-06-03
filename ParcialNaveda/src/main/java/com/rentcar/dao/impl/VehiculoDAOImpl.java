package com.rentcar.dao.impl;

import com.rentcar.dao.VehiculoDAO;
import com.rentcar.model.Vehiculo;
import com.rentcar.util.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAOImpl implements VehiculoDAO {
    private static final Logger logger = LogManager.getLogger(VehiculoDAOImpl.class);

    @Override
    public Vehiculo create(Vehiculo vehiculo) throws SQLException {
        String SQL = "INSERT INTO Vehiculos (marca, modelo, anio, patente, disponible) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, vehiculo.getMarca());
            pstmt.setString(2, vehiculo.getModelo());
            pstmt.setInt(3, vehiculo.getAnio());
            pstmt.setString(4, vehiculo.getPatente());
            pstmt.setBoolean(5, vehiculo.isDisponible());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Creación de vehículo fallida, no se afectaron filas.");
                throw new SQLException("Creación de vehículo fallida, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehiculo.setId(generatedKeys.getInt(1));
                    logger.info("Vehículo creado con ID: " + vehiculo.getId() + " - " + vehiculo.getMarca() + " " + vehiculo.getModelo());
                    return vehiculo;
                } else {
                    logger.error("Creación de vehículo fallida, no se obtuvo ID.");
                    throw new SQLException("Creación de vehículo fallida, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al crear vehículo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Vehiculo getById(int id) throws SQLException {
        String SQL = "SELECT id, marca, modelo, anio, patente, disponible FROM Vehiculos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Vehiculo vehiculo = new Vehiculo(
                            rs.getInt("id"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getInt("anio"),
                            rs.getString("patente"),
                            rs.getBoolean("disponible")
                    );
                    logger.debug("Vehículo encontrado por ID " + id + ": " + vehiculo.getPatente());
                    return vehiculo;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener vehículo por ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
        logger.debug("Vehículo con ID " + id + " no encontrado.");
        return null;
    }

    @Override
    public List<Vehiculo> getAll() throws SQLException {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String SQL = "SELECT id, marca, modelo, anio, patente, disponible FROM Vehiculos";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Vehiculo vehiculo = new Vehiculo(
                        rs.getInt("id"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("anio"),
                        rs.getString("patente"),
                        rs.getBoolean("disponible")
                );
                vehiculos.add(vehiculo);
            }
            logger.info("Listados " + vehiculos.size() + " vehículos.");
            return vehiculos;
        } catch (SQLException e) {
            logger.error("Error al obtener todos los vehículos: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean update(Vehiculo vehiculo) throws SQLException {
        String SQL = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ?, patente = ?, disponible = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, vehiculo.getMarca());
            pstmt.setString(2, vehiculo.getModelo());
            pstmt.setInt(3, vehiculo.getAnio());
            pstmt.setString(4, vehiculo.getPatente());
            pstmt.setBoolean(5, vehiculo.isDisponible());
            pstmt.setInt(6, vehiculo.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Vehículo actualizado con ID: " + vehiculo.getId());
                return true;
            } else {
                logger.warn("No se encontró vehículo con ID " + vehiculo.getId() + " para actualizar.");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar vehículo con ID " + vehiculo.getId() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String SQL = "DELETE FROM Vehiculos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Vehículo eliminado con ID: " + id);
                return true;
            } else {
                logger.warn("No se encontró vehículo con ID " + id + " para eliminar.");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar vehículo con ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
    }
}