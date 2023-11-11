package com.example.eramonmanager.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Reservaciones {
    private String idReservacion;
    private String nombre;
    private int dui;
    private int tel;
    private int cantidadPersonas;
    private String rescursos;
    private String dateReservation;

    private String fechaSalida;

    private double PrecioReservacion;
    private String estado;
    private String comprobantePago; // Ruta de la foto, puede ser un Uri o un String dependiendo de tus necesidades

    // Constructor vacío requerido por Firebase
    public Reservaciones() {}

    // Constructor con parámetros
    public Reservaciones(String idReservacion, String nombre, int dui, int tel, int cantidadPersonas, String rescursos, String dateReservation, String fechaSalida, double precioReservacion, String estado, String comprobantePago) {
     this.idReservacion=idReservacion;
        this.nombre = nombre;
        this.dui = dui;
        this.tel = tel;
        this.cantidadPersonas = cantidadPersonas;
        this.rescursos = rescursos;
        this.dateReservation = dateReservation;
        this.fechaSalida = fechaSalida;
        PrecioReservacion = precioReservacion;
        this.estado = estado;
        this.comprobantePago = comprobantePago;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDui() {
        return dui;
    }

    public void setDui(int dui) {
        this.dui = dui;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public String getRescursos() {
        return rescursos;
    }

    public void setRescursos(String rescursos) {
        this.rescursos = rescursos;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public double getPrecioReservacion() {
        return PrecioReservacion;
    }

    public void setPrecioReservacion(double precioReservacion) {
        PrecioReservacion = precioReservacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComprobantePago() {
        return comprobantePago;
    }

    public void setComprobantePago(String comprobantePago) {
        this.comprobantePago = comprobantePago;
    }

    public void crearReservacion(String idReservacion, String nombre, int dui, int tel, int cantidadPersonas, String recursos, String dateReservation, String fechaSalida, double precioReservacion, String estado, String comprobantePago) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reservacionesRef = database.getReference("Reservaciones");

        Reservaciones reservacion = new Reservaciones(idReservacion, nombre, dui, tel, cantidadPersonas, recursos, dateReservation, fechaSalida, precioReservacion, estado, comprobantePago);

        reservacionesRef.child(idReservacion).setValue(reservacion);
    }

}
