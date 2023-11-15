package com.example.eramonmanager.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.eramonmanager.Activity.Recursos.MostrarRecursosCallback;
import com.example.eramonmanager.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

public class AddReservationActivity extends AppCompatActivity {


    private ChipGroup chipGroup;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private String imageUrl1;
    private static final int PICK_IMAGE_REQUEST1 = 1;
    private Button imageButton1;
   private TextView pageTitleTextView;
    boolean isEditMode = false;

    EditText titleEditText,teledit,duiedit,cantidadedit,reservacionedit,salidaedit,precoedit;
    private ImageButton seleccionarButton;
    private boolean[] selectedOptions;
    private String[] options;

    public String info;

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


        //titleEditText = findViewById(R.id.Add_Reservation_Name);




        pageTitleTextView = findViewById(R.id.pagetitle);
titleEditText=findViewById(R.id.Add_Reservation_Name);
duiedit=findViewById(R.id.Add_Reservation_Dui);
teledit=findViewById(R.id.Add_Reservation_Tel);
precoedit=findViewById(R.id.Add_Reservation_ReservationPrice);
reservacionedit=findViewById(R.id.Add_Reservation_DateReservation);
salidaedit=findViewById(R.id.Add_Reservation_DateOut);
cantidadedit=  findViewById(R.id.Add_Reservation_AmountPeople);

        //receive data


        String title = getIntent().getStringExtra("title");
        int dui = getIntent().getIntExtra("dui", 0);  // 0 es el valor predeterminado si "dui" no está presente
        int tel = getIntent().getIntExtra("tel", 0);  // 0 es el valor predeterminado si "tel" no está presente
        int cantidadp = getIntent().getIntExtra("cantidad", 0);
        double precio = getIntent().getDoubleExtra("precio", 0.0);  // 0.0 es el valor predeterminado si "precio" no está presente
        String fechareservacion = getIntent().getStringExtra("reservacion");
        String fechasalida = getIntent().getStringExtra("salida");
        String docId = getIntent().getStringExtra("docId");
        titleEditText.setText(title);
        teledit.setText(String.valueOf(tel));
        duiedit.setText(String.valueOf(dui));
        cantidadedit.setText(String.valueOf(cantidadp));
        precoedit.setText(String.valueOf(precio));
        reservacionedit.setText(fechareservacion);
        salidaedit.setText(fechasalida);
        Log.d("AddReservationActivity", "docId: " + docId);

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }



        if(isEditMode){
            pageTitleTextView.setText("Edit your note");

        }





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

        obtenerNombresRecursos();

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

                // if (!TextUtils.isEmpty(imageUrl1)) {

                String idReservacion = mDatabase.child("Reservaciones").push().getKey();
                RadioGroup radioGroup = findViewById(R.id.radioGroupStatus);
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                String estado = obtenerEstadoPorRadioButtonId(radioButtonId);

                Reservaciones reservaciones = new Reservaciones();
                if (isEditMode) {
                    pageTitleTextView.setText("edicion");
                    // Modo de edición: Actualizar la reserva existente
                    reservaciones.actualizarReserva(
                            docId, nombreReservacion, dui, tel, cantidadPeople, info,
                            dateReservationStr, dateOutStr, precioReservacion, estado, imageUrl1);
                } else {
                    // Modo de creación: Crear una nueva reserva
                    reservaciones.crearReservacion(
                            idReservacion, nombreReservacion, dui, tel, cantidadPeople, info,
                            dateReservationStr, dateOutStr, precioReservacion, estado, imageUrl1);
                }

                // Cerrar la actividad o realizar otras acciones según sea necesario
                finish();
            }
        });










        seleccionarButton = findViewById(R.id.SelectResources);


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
    private void obtenerNombresRecursos() {
        Recursos recursos = new Recursos();
        recursos.mostrarRecursos(new Recursos.MostrarRecursosCallback() {
            @Override
            public void onResultado(List<String> nombresRecursos) {
                // Actualiza la variable 'options' con los nombres de recursos obtenidos
                options = nombresRecursos.toArray(new String[0]);
            }
        });
    }

    private void showOptionSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona las opciones");

        // Crear una lista de vistas que contengan un TextView y un EditText para cada opción
        List<View> optionViews = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            View optionView = getLayoutInflater().inflate(R.layout.item_dialog_option, null);

            // Configurar el TextView con la opción
            TextView optionTextView = optionView.findViewById(R.id.dialogOptionTextView);
            optionTextView.setText(options[i]);

            // Configurar el EditText para la cantidad (solo números)
            EditText editText = optionView.findViewById(R.id.dialogEditText);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            // Agregar la vista a la lista
            optionViews.add(optionView);
        }

        // Agregar las vistas a un LinearLayout dentro del diálogo
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (View optionView : optionViews) {
            linearLayout.addView(optionView);
        }

        builder.setView(linearLayout);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateChipGroup(optionViews);
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void updateChipGroup(List<View> optionViews) {
        chipGroup.removeAllViews();

        for (int i = 0; i < options.length; i++) {
            View optionView = optionViews.get(i);
            TextView optionTextView = optionView.findViewById(R.id.dialogOptionTextView);
            EditText editText = optionView.findViewById(R.id.dialogEditText);

            if (!editText.getText().toString().isEmpty()) {
                Chip dynamicChip = new Chip(this);
                dynamicChip.setText(options[i] + " -Cant. " + editText.getText().toString());
                info += options[i] + " -Cant. " + editText.getText().toString() + ", ";
                dynamicChip.setCloseIconVisible(true);
                dynamicChip.setCheckable(false);
                final int position = i;
                dynamicChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.getText().clear();
                        updateChipGroup(optionViews);
                    }
                });
                chipGroup.addView(dynamicChip);
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