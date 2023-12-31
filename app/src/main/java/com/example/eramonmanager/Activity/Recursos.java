package com.example.eramonmanager.Activity;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recursos implements Serializable {
    private String idRecursos;
    private String nombreRecurso;
    private int cantidadRecurso;
    private String imagenUrl;
    private String fechaActualizacion;

    // Constructor vacío requerido para Firebase
    public Recursos() {
        // Constructor vacío requerido para Firebase
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }


    public Recursos(String idRecursos, String nombreRecurso, int cantidadRecurso, String imagenUrl, String fechaActualizacion) {
        this.idRecursos = idRecursos;
        this.nombreRecurso = nombreRecurso;
        this.cantidadRecurso = cantidadRecurso;
        this.imagenUrl = imagenUrl;
        this.fechaActualizacion = fechaActualizacion;
    }


    public String getIdRecursos() {
        return idRecursos;
    }


    public void setIdRecursos(String idRecursos) {
        this.idRecursos = idRecursos;
    }


    public String getNombreRecurso() {
        return nombreRecurso;
    }


    public void setNombreRecurso(String nombreRecurso) {
        this.nombreRecurso = nombreRecurso;
    }


    public int  getCantidadRecurso() {
        return cantidadRecurso;
    }


    public void setCantidadRecurso(int  cantidadRecurso) {
        this.cantidadRecurso = cantidadRecurso;
    }



    public interface BusquedaCallback {
        void onResultado(boolean encontrado);
    }

    public void buscarEnFirebase(String dato, BusquedaCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recursosRef = database.getReference("Recursos");

        recursosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean encontrado = false;

                for (DataSnapshot nodeSnapshot : dataSnapshot.getChildren()) {
                    String idRecursos = nodeSnapshot.child("idRecursos").getValue(String.class);
                    if (idRecursos != null && idRecursos.equals(dato)) {
                        encontrado = true;
                        break;
                    }
                }

                // Llama al método de devolución de llamada con el resultado
                callback.onResultado(encontrado);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores
            }
        });
    }

    public void Actualizar(String idRecurso, int nuevaCantidad, String nuevoNombre, String nuevaImagenUrl, String nuevaFecha) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recursos");
        DatabaseReference reservaRef = databaseReference.child(idRecurso);

        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("nombreRecurso", nuevoNombre);
        actualizacion.put("cantidadRecurso", nuevaCantidad);
        actualizacion.put("imagenUrl", nuevaImagenUrl);
        actualizacion.put("fechaActualizacion", nuevaFecha);

        // Aplicar la actualización a la base de datos
        reservaRef.updateChildren(actualizacion);
    }


    public void Crear(String idRecursos, String nombreRecurso, int cantidad, String imagenUrl, String fecha) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recursosRef = database.getReference("Recursos");

        Recursos recurso = new Recursos(idRecursos, nombreRecurso, cantidad, imagenUrl, fecha);

        recursosRef.child(idRecursos).setValue(recurso);
    }


    public static void Eliminar(String resourceId, String imageUrl) {     // Eliminar de Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recursosRef = database.getReference("Recursos");
        recursosRef.child(resourceId).removeValue();

        // Eliminar de Firebase Storage
        if (imageUrl != null && !imageUrl.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
            storageRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("Recursos", "Imagen eliminada del almacenamiento con éxito");
            }).addOnFailureListener(exception -> {
                Log.e("Recursos", "Error al eliminar la imagen del almacenamiento", exception);
            });
        }
        Log.d("Recursos", "Recurso eliminado con éxito: " + resourceId);
    }


    public interface MostrarRecursosCallback {
        void onResultado(List<String> nombresRecursos);
    }

    public void mostrarRecursos(MostrarRecursosCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recursosRef = database.getReference("Recursos");

        recursosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> nombresRecursos = new ArrayList<>();

                for (DataSnapshot nodeSnapshot : dataSnapshot.getChildren()) {
                    String nombreRecurso = nodeSnapshot.child("nombreRecurso").getValue(String.class);
                    if (nombreRecurso != null) {
                        nombresRecursos.add(nombreRecurso);
                    }
                }

                // Llama al método de devolución de llamada con los nombres de recursos
                callback.onResultado(nombresRecursos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores
            }
        });
    }

}