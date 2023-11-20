package com.example.eramonmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.eramonmanager.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddResourceActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private String imageUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button imageButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            ImageView imagen = findViewById(R.id.ImageResource);

            // Escala la imagen para que se ajuste al tamaño del ImageButton
            Picasso.get().load(filePath).resize(imagen.getWidth(), imagen.getHeight()).centerCrop().into(imagen);

            imageUrl = filePath.toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        imageButton = findViewById(R.id.Button_UploadPhotoVoucher);

        Button button = findViewById(R.id.Button_AddResource);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nombreRecurso1 = findViewById(R.id.Add_Resource_Name);
                EditText Cantidad1 = findViewById(R.id.Add_Resource_Mount);
                ShapeableImageView ImageResource = findViewById(R.id.ImageResource);

                //se obtienen los valores ingresados por el usuario en los campos de nombre y cantidad
                String nombreRecurso = nombreRecurso1.getText().toString();
                String Cantidad = Cantidad1.getText().toString();
                String idRecursos = nombreRecurso1.getText().toString();

                // verifica si el título es nulo o está vacío. Si es así, se muestra un mensaje de error
                if (nombreRecurso==null || nombreRecurso.isEmpty()) {
                    nombreRecurso1.setError("El nombre es requerido");
                    return;
                }
                // verifica si la cantidad es nulo o está vacío. Si es así, se muestra un mensaje de error
                if (Cantidad==null || Cantidad.isEmpty()) {
                    Cantidad1.setError("La cantidad es requerida");
                    return;
                }

                if (!TextUtils.isEmpty(imageUrl)) {
                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaActual = dateFormat.format(date);

                    Recursos recursos = new Recursos();

                    recursos.buscarEnFirebase(idRecursos, new Recursos.BusquedaCallback() {
                        @Override
                        public void onResultado(boolean encontrado) {
                            if (encontrado) {
                                recursos.Actualizar(idRecursos, Integer.parseInt(Cantidad), nombreRecurso, imageUrl, fechaActual);
                            } else {
                                recursos.Crear(idRecursos, nombreRecurso, Integer.parseInt(Cantidad), imageUrl, fechaActual);
                            }

                            //Se guardo con exito
                            Toast.makeText(AddResourceActivity.this, "El recurso se ha guardado exitosamente", Toast.LENGTH_SHORT).show();

                            // Limpiar los campos después de agregar con éxito
                            nombreRecurso1.setText("");
                            Cantidad1.setText("");
                            ImageResource.setImageResource(R.drawable.glamping);
                        }
                    });
                }
                else {
                    Toast.makeText(AddResourceActivity.this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenGallery();
            }
        });

    }

    private void toOpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


}