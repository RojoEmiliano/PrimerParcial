package com.rentcar.model;

import java.time.LocalDate;

public class Alquiler {
    private int id;
    private int idVehiculo;
    private int idCliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin; // Puede ser null si el alquiler está activo

    // Constructor para crear nuevos alquileres (sin ID y sin fechaFin inicial)
    public Alquiler(int idVehiculo, int idCliente, LocalDate fechaInicio) {
        this.idVehiculo = idVehiculo;
        this.idCliente = idCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = null; // Inicialmente no tiene fecha de fin
    }

    // Constructor para recuperar alquileres de la DB (con ID y posible fechaFin)
    public Alquiler(int id, int idVehiculo, int idCliente, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.idCliente = idCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActivo() {
        return fechaFin == null;
    }

    @Override
    public String toString() {
        String estado = (fechaFin == null) ? "Activo" : "Finalizado";
        return "Alquiler{" +
                "id=" + id +
                ", idVehiculo=" + idVehiculo +
                ", idCliente=" + idCliente +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + (fechaFin != null ? fechaFin : "N/A") +
                ", estado=" + estado +
                '}';
    }
}