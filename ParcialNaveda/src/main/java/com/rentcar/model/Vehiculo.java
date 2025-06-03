package com.rentcar.model;

public class Vehiculo {
    private int id;
    private String marca;
    private String modelo;
    private int anio;
    private String patente;
    private boolean disponible;

    // Constructor para crear nuevos vehículos (sin ID, ya que la DB lo genera)
    public Vehiculo(String marca, String modelo, int anio, String patente, boolean disponible) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.patente = patente;
        this.disponible = disponible;
    }

    // Constructor para recuperar vehículos de la DB (con ID)
    public Vehiculo(int id, String marca, String modelo, int anio, String patente, boolean disponible) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.patente = patente;
        this.disponible = disponible;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "id=" + id +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", patente='" + patente + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}