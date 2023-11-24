package com.example.eramonmanager.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eramonmanager.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddResourceActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int MY_PERMISSIONS_REQUEST_MANAGE_EXTERNAL_STORAGE = 2;
    //Datos que el adapter utilizara para llenar el RecyclerView

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private String imageUrl;
    private boolean isImageUploaded = false;
    private String idRecursos;
    private Button imageButton;
    private String currentImageFileName;


    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri filePath = result.getData().getData();
                    uploadImage(filePath);

                    ImageView imagen = findViewById(R.id.ImageResource);
                    Picasso.get().load(filePath).resize(imagen.getWidth(), imagen.getHeight()).centerCrop().into(imagen);
                }
            }
    );

    private ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                if (permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        permissions.get(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Permiso denegado. No se puede acceder a la galería.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        imageButton = findViewById(R.id.Button_UploadPhotoVoucher);

        Button button = findViewById(R.id.Button_AddResource);
        button.setOnClickListener(v -> {
            EditText nombreRecurso1 = findViewById(R.id.Add_Resource_Name);
            EditText Cantidad1 = findViewById(R.id.Add_Resource_Mount);
            ShapeableImageView ImageResource = findViewById(R.id.ImageResource);

            String nombreRecurso = nombreRecurso1.getText().toString();
            String Cantidad = Cantidad1.getText().toString();
            idRecursos = nombreRecurso1.getText().toString();

            if (nombreRecurso.isEmpty()) {
                nombreRecurso1.setError("El nombre es requerido");
                return;
            } else if (Cantidad.isEmpty()) {
                Cantidad1.setError("La cantidad es requerida");
                return;
            }

            // Obtener datos del Intent
            Intent intent = getIntent();
            if (intent.hasExtra("recursoSeleccionado")) {
                Recursos recursoSeleccionado = (Recursos) intent.getSerializableExtra("recursoSeleccionado");

                // Si no se ha subido una nueva imagen, mantén la imagen existente
                if (!isImageUploaded) {
                    imageUrl = recursoSeleccionado.getImagenUrl();
                }

                // Actualizar el recurso existente
                actualizarRecursoEnFirebase(recursoSeleccionado.getIdRecursos(), nombreRecurso, Integer.parseInt(Cantidad), imageUrl);
            } else {

                if (!isImageUploaded) {
                    Toast.makeText(AddResourceActivity.this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear un nuevo recurso si no existe
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String fechaActual = dateFormat.format(date);
                Recursos recursos = new Recursos();

                recursos.Crear(idRecursos, nombreRecurso, Integer.parseInt(Cantidad), imageUrl, fechaActual);

                // Se guardó con éxito
                Toast.makeText(AddResourceActivity.this, "El recurso se ha guardado exitosamente", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        // Obtener datos del Intent
        Intent intent = getIntent();
        if (intent.hasExtra("recursoSeleccionado")) {
            Recursos recursoSeleccionado = (Recursos) intent.getSerializableExtra("recursoSeleccionado");

            // Actualizar la interfaz con los detalles del recurso
            updateUIWithResourceDetails(recursoSeleccionado);
        }

        imageButton.setOnClickListener(view -> {
            // Verificar si hay una imagen actual
            if (currentImageFileName != null) {
                // Eliminar la imagen actual de Firebase Storage
                StorageReference existingImageRef = storage.getReference().child(currentImageFileName);
                existingImageRef.delete().addOnSuccessListener(aVoid -> {
                    // Después de eliminar la imagen existente, subir la nueva imagen
                    checkAndOpenGallery();
                }).addOnFailureListener(exception -> {
                    // Manejar fallos al eliminar la imagen existente
                    Toast.makeText(AddResourceActivity.this, "Error al eliminar la imagen existente", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Si no hay una imagen actual, simplemente pedir al usuario que seleccione una nueva imagen
                checkAndOpenGallery();
            }
        });


        imageButton.setOnClickListener(view -> checkAndOpenGallery());
    }

    private void updateUIWithResourceDetails(Recursos recurso) {
        // Actualiza la interfaz con los detalles del recurso (por ejemplo, setText en TextViews, carga de imagen, etc.)
        EditText nombreRecurso1 = findViewById(R.id.Add_Resource_Name);
        EditText cantidad1 = findViewById(R.id.Add_Resource_Mount);
        ShapeableImageView imageResource = findViewById(R.id.ImageResource);

        nombreRecurso1.setText(recurso.getNombreRecurso());
        cantidad1.setText(String.valueOf(recurso.getCantidadRecurso()));

        // Carga la imagen con Glide o Picasso
        Glide.with(this)
                .load(recurso.getImagenUrl())
                .centerCrop()
                .into(imageResource);

        currentImageFileName = getFileNameFromUrl(recurso.getImagenUrl());

    }

    private String getFileNameFromUrl(String url) {
        Uri uri = Uri.parse(url);
        return uri.getLastPathSegment();
    }


    private final ActivityResultLauncher<Intent> manageStoragePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                openGallery();
                            }
                        }
                    });

    private void checkAndOpenGallery() {
        // Comprueba la versión del sistema operativo, si es Android 11 o superior
        // Verifica si tiene el permiso para administrar el almacenamiento externo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Verifica si la app tiene permiso en Android 11 o superior
            if (Environment.isExternalStorageManager()) {
                openGallery();
            } else {
                // Verifica que se obtenga el permiso si aún no se tiene
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                manageStoragePermissionLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Verifican si se tiene permiso en Android 6.0 o superior, pero inferior a Android 11
                openGallery();
            } else {
                // Solicita permiso de lectura
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            openGallery();
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            // Verificar si hay una imagen actual
            if (currentImageFileName != null) {
                // Eliminar la imagen actual de Firebase Storage
                StorageReference existingImageRef = storage.getReference().child(currentImageFileName);
                existingImageRef.delete().addOnSuccessListener(aVoid -> {
                    // Después de eliminar la imagen existente, subir la nueva imagen
                    uploadNewImage(filePath);
                }).addOnFailureListener(exception -> {
                    // Manejar fallos al eliminar la imagen existente
                    Toast.makeText(AddResourceActivity.this, "Error al eliminar la imagen existente", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Si no hay una imagen actual, simplemente subir la nueva imagen
                uploadNewImage(filePath);
            }
        }
    }

    private void uploadNewImage(Uri filePath) {
        String nombreImagen = "images/" + System.currentTimeMillis() + ".jpg";

        // Mostrar mensaje de subida en proceso
        Toast.makeText(AddResourceActivity.this, "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        storage.getReference().child(nombreImagen).putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de descarga de la imagen subida
                    storage.getReference().child(nombreImagen).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                imageUrl = uri.toString();
                                isImageUploaded = true;

                                // Mostrar mensaje de éxito
                                Toast.makeText(AddResourceActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Manejar fallos
                                Toast.makeText(AddResourceActivity.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Manejar fallos
                    Toast.makeText(AddResourceActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                });
    }


    // Métodos de actualización
    private void actualizarRecursoEnFirebase(String idRecursos, String nuevoNombre, int nuevaCantidad, String nuevaImageUrl) {
        // Obtener la fecha actual
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaActual = dateFormat.format(date);

        // Actualizar el recurso en Firebase
        DatabaseReference recursoRef = mDatabase.child("Recursos").child(idRecursos);
        recursoRef.child("nombreRecurso").setValue(nuevoNombre);
        recursoRef.child("cantidadRecurso").setValue(nuevaCantidad);
        recursoRef.child("imagenUrl").setValue(nuevaImageUrl);
        recursoRef.child("fechaActualizacion").setValue(fechaActual);

        // Mostrar mensaje de éxito
        Toast.makeText(AddResourceActivity.this, "Recurso actualizado con éxito", Toast.LENGTH_SHORT).show();

        finish();
    }

}