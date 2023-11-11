package com.example.eramonmanager.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.eramonmanager.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class AddReservationActivity extends AppCompatActivity {


    private ChipGroup chipGroup;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private String imageUrl1;
    private static final int PICK_IMAGE_REQUEST1 = 1;
    private Button imageButton1;


    private ImageButton seleccionarButton;
    private boolean[] selectedOptions;
    private String[] options;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();



            // Escala la imagen para que se ajuste al tamaño del ImageButton

            imageUrl1 = filePath.toString();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        imageButton1 = findViewById(R.id.Button_UploadPhoto);

        Button button1 = findViewById(R.id.Button_AddReservation);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView nombreReservacionTextView = findViewById(R.id.Add_Reservation_Name);
                EditText duiEditText = findViewById(R.id.Add_Reservation_Dui);
                EditText telEditText = findViewById(R.id.Add_Reservation_Tel);
                EditText cantidadPeopleEditText = findViewById(R.id.Add_Reservation_AmountPeople);
                EditText dateReservationEditText = findViewById(R.id.Add_Reservation_DateReservation);
                EditText dateOutEditText = findViewById(R.id.Add_Reservation_DateOut);
                EditText priceEditText = findViewById(R.id.Add_Reservation_ReservationPrice);

                String nombreReservacion = nombreReservacionTextView.getText().toString();
                String duiStr = duiEditText.getText().toString();
                String telStr = telEditText.getText().toString();
                String cantidadPeopleStr = cantidadPeopleEditText.getText().toString();
                String dateReservationStr = dateReservationEditText.getText().toString();
                String dateOutStr = dateOutEditText.getText().toString();
                String priceStr = priceEditText.getText().toString();

                int dui = Integer.parseInt(duiStr);
                int tel = Integer.parseInt(telStr);
                int cantidadPeople = Integer.parseInt(cantidadPeopleStr);
                double precioReservacion = Double.parseDouble(priceStr);

                if (!TextUtils.isEmpty(imageUrl1)) {


                    String idReservacion = mDatabase.child("Reservaciones").push().getKey();
                    RadioGroup radioGroup = findViewById(R.id.radioGroupStatus);
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    String estado = obtenerEstadoPorRadioButtonId(radioButtonId);

                    Reservaciones reservaciones = new Reservaciones();


                    reservaciones.crearReservacion(idReservacion, nombreReservacion, dui, tel, cantidadPeople, "aosjdopa",
                            dateReservationStr, dateOutStr, precioReservacion, estado, imageUrl1);


                }
            }



        });





        chipGroup = findViewById(R.id.ChipGroupResources);
        seleccionarButton = findViewById(R.id.SelectResources);

        options = getResources().getStringArray(R.array.resource_array);
        selectedOptions = new boolean[options.length];

        seleccionarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionSelectionDialog();
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenGallery();
            }
        });
    }


    private void showOptionSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona las opciones");
        builder.setMultiChoiceItems(options, selectedOptions, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedOptions[which] = isChecked;
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateChipGroup();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void updateChipGroup() {
        chipGroup.removeAllViews();
        for (int i = 0; i < options.length; i++) {
            if (selectedOptions[i]) {
                Chip chip = new Chip(this);
                chip.setText(options[i]);
                chip.setCloseIconVisible(true);
                chip.setCheckable(false);
                final int position = i;
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedOptions[position] = false;
                        updateChipGroup();
                    }
                });
                chipGroup.addView(chip);
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    private String obtenerEstadoPorRadioButtonId(int radioButtonId) {
        if (radioButtonId == R.id.radioButtonCancelled) {
            return "Cancelado";
        } else if (radioButtonId == R.id.radioButtonPending) {
            return "Pendiente";
        } else {
            return "Desconocido";
        }
    }
    private void toOpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST1);
    }


}