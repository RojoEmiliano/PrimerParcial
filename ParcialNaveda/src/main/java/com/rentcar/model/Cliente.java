package com.rentcar.model;

public class Cliente {
    private int id;
    private String nombre;
    private String apellido;
    private String dni;
    private String numeroLicencia;
    private String telefono;

    // Constructor para crear nuevos clientes
    public Cliente(String nombre, String apellido, String dni, String numeroLicencia, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.numeroLicencia = numeroLicencia;
        this.telefono = telefono;
    }

    // Constructor para recuperar clientes de la DB
    public Cliente(int id, String nombre, String apellido, String dni, String numeroLicencia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.numeroLicencia = numeroLicencia;
        this.telefono = telefono;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", numeroLicencia='" + numeroLicencia + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}