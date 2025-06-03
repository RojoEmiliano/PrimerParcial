package com.rentcar.dao;

import com.rentcar.model.Alquiler;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AlquilerDAO extends GenericDAO<Alquiler> {
    // Métodos específicos para la gestión de alquileres con lógica transaccional
    boolean registrarAlquiler(int idVehiculo, int idCliente, LocalDate fechaInicio) throws SQLException;
    boolean registrarDevolucion(int idAlquiler) throws SQLException;
    List<Alquiler> getAlquileresActivos() throws SQLException; // Alquileres que no han sido devueltos
    List<Alquiler> getAlquileresByClienteId(int clienteId) throws SQLException;
    List<Alquiler> getAlquileresByVehiculoId(int vehiculoId) throws SQLException; // Para verificar antes de eliminar
}