

package com.example.eramonmanager.Activity;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Reservaciones implements Serializable {
    private String idReservacion;
    private String nombre;
    private String dui;
    private String tel;
    private String cantidadPersonas;
    private String rescursos;
    private String dateReservation;

    private String fechaSalida;

    private String PrecioReservacion;
    private String estado;
    private String comprobantePago; // Ruta de la foto, puede ser un Uri o un String dependiendo de tus necesidades

    // Constructor vacío requerido por Firebase
    public Reservaciones() {}

    // Constructor con parámetros
    public Reservaciones(String idReservacion, String nombre, String dui, String tel, String cantidadPersonas, String rescursos, String dateReservation, String fechaSalida, String precioReservacion, String estado, String comprobantePago) {
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

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(String cantidadPersonas) {
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

    public String getPrecioReservacion() {
        return PrecioReservacion;
    }

    public void setPrecioReservacion(String precioReservacion) {
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

    public String getId() {
        return idReservacion;
    }

    public void setId(String Id) {
        this.idReservacion = Id;
    }

    public void crearReservacion(String nombre, String dui, String tel, String cantidadPersonas, String recursos,
                                 String dateReservation, String fechaSalida, String precioReservacion, String estado, String comprobantePago) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reservacionesRef = database.getReference("Reservaciones");

        // Utiliza push() para generar un ID único
        String idReservacion = reservacionesRef.push().getKey();

        Reservaciones reservacion = new Reservaciones(idReservacion, nombre, dui, tel, cantidadPersonas, recursos, dateReservation, fechaSalida, precioReservacion, estado, comprobantePago);

        // Guarda la reservación con el ID generado
        reservacionesRef.child(idReservacion).setValue(reservacion);
    }


    public static void EliminarR(String resourceId,  String imageUrl) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recursosRef = database.getReference("Reservaciones");
        String idRecursosAEliminar = resourceId;
        recursosRef.child(idRecursosAEliminar).removeValue();

        // Eliminar de Firebase Storage
        if (imageUrl != null && !imageUrl.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
            storageRef.delete().addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(exception -> {
                Log.e("Recursos", "Error al eliminar la imagen del almacenamiento", exception);
            });
        }
        Log.d("Recursos", "Recurso eliminado con éxito: " + resourceId);

    }
    public void actualizarReserva(String idReservacion, String nombreReservacion, String dui, String tel, String cantidadPeople,
                                  String info, String dateReservation, String dateOut, String precioReservacion,
                                  String estado, String imageUrl) {
        // Obtén una referencia a la base de datos Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reservaciones");

        // Obtén una referencia a la reserva que deseas actualizar utilizando su ID
        DatabaseReference reservaRef = databaseReference.child(idReservacion);

        Reservaciones reservacionActualizada = new Reservaciones(
                idReservacion, nombreReservacion, dui, tel, cantidadPeople,
                info, dateReservation, dateOut, precioReservacion,
                estado, imageUrl);

        // Guarda la reservación actualizada en la base de datos, reemplazando la existente
        reservaRef.setValue(reservacionActualizada)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La actualización fue exitosa
                        Log.d(TAG, "Reserva actualizada con éxito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La actualización falló
                        Log.e(TAG, "Error al actualizar la reserva", e);
                    }
                });
    }


}