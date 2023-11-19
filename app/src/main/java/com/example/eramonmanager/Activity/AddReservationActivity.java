package com.example.eramonmanager.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.eramonmanager.Activity.Recursos.MostrarRecursosCallback;
import com.example.eramonmanager.Adapters.HomeAdapter;
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
    static TextView pageTitleTextView;
    boolean isEditMode = false;

    EditText titleEditText,teledit,duiedit,cantidadedit,reservacionedit,salidaedit,precoedit;
    private ImageButton seleccionarButton;
    private boolean[] selectedOptions;
    private String[] options;
    RadioGroup estadoR;

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


        boolean editable = getIntent().getBooleanExtra("editable", true);


        pageTitleTextView = findViewById(R.id.pagetitle);

        titleEditText=findViewById(R.id.Add_Reservation_Name);
        titleEditText.setEnabled(editable);

        duiedit=findViewById(R.id.Add_Reservation_Dui);
        duiedit.setEnabled(editable);

        teledit=findViewById(R.id.Add_Reservation_Tel);
        teledit.setEnabled(editable);

        precoedit=findViewById(R.id.Add_Reservation_ReservationPrice);
        precoedit.setEnabled(editable);

        reservacionedit=findViewById(R.id.Add_Reservation_DateReservation);
        reservacionedit.setEnabled(editable);

        salidaedit=findViewById(R.id.Add_Reservation_DateOut);
        salidaedit.setEnabled(editable);

        cantidadedit=  findViewById(R.id.Add_Reservation_AmountPeople);
        cantidadedit.setEnabled(editable);

        //receive data
        String title = getIntent().getStringExtra("title");
        int dui = getIntent().getIntExtra("dui", 0);  // 0 es el valor predeterminado si "dui" no está presente
        int tel = getIntent().getIntExtra("tel", 0);  // 0 es el valor predeterminado si "tel" no está presente
        int cantidadp = getIntent().getIntExtra("cantidad", 0);
        double precio = getIntent().getDoubleExtra("precio", 0.0);  // 0.0 es el valor predeterminado si "precio" no está presente
        String fechareservacion = getIntent().getStringExtra("reservacion");
        String fechasalida = getIntent().getStringExtra("salida");
        //String docId = getIntent().getStringExtra("docId");

        reservacionedit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Fecha actual
                final Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                //Creacion de un DatePicketDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddReservationActivity.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mes, int dia) {
                        //Se selecciona una fecha que se incorporara al editext
                        reservacionedit.setText(dia + "/" + (mes + 1) + "/" + year); //formato de la fecha
                    }
                }, year, month, day);

                // la fecha actual como fecha mínima
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                //Toast.makeText(AddReservationActivity.this, "No se puede seleccionar una fecha anterior", Toast.LENGTH_SHORT).show();

                datePickerDialog.show();
            }
        });

        salidaedit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                    //Fecha actual
                    final Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    //Creacion de un DatePicketDialog
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddReservationActivity.this, new DatePickerDialog.OnDateSetListener(){
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int mes, int dia) {
                            //Se selecciona una fecha que se incorporara al editext
                            salidaedit.setText(dia + "/" + (mes + 1) + "/" + year); //formato de la fecha
                        }
                    }, year, month, day);

                    //Obtener la fecha de reservacionedit y convertirla a milisegundos
                    String reserva = reservacionedit.getText().toString();
                    SimpleDateFormat ObtenerFecha = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date = ObtenerFecha.parse(reserva);
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.set(Calendar.HOUR_OF_DAY, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);
                        long millis = c.getTimeInMillis();

                        //Establecer la fecha mínima como el inicio del día de la fecha de reservacionedit
                        datePickerDialog.getDatePicker().setMinDate(millis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //Mostrar un mensaje al usuario
                    //Toast.makeText(AddReservationActivity.this, "No puedes seleccionar una fecha anterior a la fecha de reserva", Toast.LENGTH_LONG).show();

                    datePickerDialog.show();
                }
        });


        titleEditText.setText(title);
        teledit.setText(String.valueOf(tel));
        duiedit.setText(String.valueOf(dui));
        cantidadedit.setText(String.valueOf(cantidadp));
        precoedit.setText(String.valueOf(precio));
        reservacionedit.setText(fechareservacion);
        salidaedit.setText(fechasalida);
        Log.d("AddReservationActivity", "docId: " + title);

        estadoR = findViewById(R.id.radioGroupStatus);
        estadoR.setEnabled(editable);
        RadioButton radioButtonCancelled = findViewById(R.id.radioButtonCancelled);
        radioButtonCancelled.setEnabled(editable);
        RadioButton radioButtonPending = findViewById(R.id.radioButtonPending);
        radioButtonPending.setEnabled(editable);

        // Recuperar el estado de la intención
        String estado = getIntent().getStringExtra("estado");

        // Establecer el estado en el RadioButton correspondiente
        if (estado != null) {
            if (estado.equals("Cancelado")) {
                radioButtonCancelled.setChecked(true);
            } else if (estado.equals("Pendiente")) {
                radioButtonPending.setChecked(true);
            }
        }

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        //Modificacion del titulo de la activity segun corresponda
        Intent intent = getIntent();
        boolean viewDetails = intent.getBooleanExtra("viewDetails", false);
        boolean viewHistorial = intent.getBooleanExtra("viewHistorial", false);

        if (isEditMode == false) {
            pageTitleTextView.setText("Agregar Reservación");
        } else if (viewDetails) {
            pageTitleTextView.setText("Detalles de Reservación");
        }else if (viewHistorial) {
            pageTitleTextView.setText("Historial");
        } else{
            pageTitleTextView.setText("Editar Reservación");
        }

        //Obtener los recursos seleccionados para volver a mostrarlos
        chipGroup = findViewById(R.id.ChipGroupResources);

        String recursos = getIntent().getStringExtra("recursos");

        if (recursos != null) {
            // Divide la cadena de recursos en cada recurso individual
            String[] opciones = recursos.split("  ");

            for (String opcion : opciones) {
                Chip chip = new Chip(this);
                chip.setText(opcion);

                // Verifica si estás en modo de edición
                if (editable) {
                    // Configura un listener para eliminar el chip cuando se hace clic
                    chip.setClickable(true);
                   chip.setCloseIconVisible(true);
                    chip.setOnClickListener(view -> {
                        chipGroup.removeView(chip);
                        // Agrega aquí la lógica adicional para eliminar el recurso de la base de datos o donde sea necesario
                    });
                } else {
                    chip.setClickable(false);
                }

                chipGroup.addView(chip);
            }
        }

        seleccionarButton = findViewById(R.id.SelectResources);
        //seleccionarButton.setEnabled(editable);

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
        imageButton1.setEnabled(editable);

        if(editable == false){
            imageButton1.setVisibility(View.GONE);
        }else{
            imageButton1.setVisibility(View.VISIBLE);
        }

        Button button1 = findViewById(R.id.Button_AddReservation);

        if(editable == false){
            button1.setVisibility(View.GONE);
        }else{
            button1.setVisibility(View.VISIBLE);
        }

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

                    // Modo de edición: Actualizar la reserva existente
                    reservaciones.actualizarReserva(
                            docId, nombreReservacion, dui, tel, cantidadPeople, info,
                            dateReservationStr, dateOutStr, precioReservacion, estado, imageUrl1);
                } else {
                    // Modo de creación: Crear una nueva reserva
                    reservaciones.crearReservacion(
                             nombreReservacion, dui, tel, cantidadPeople, info,
                            dateReservationStr, dateOutStr, precioReservacion, estado, imageUrl1);
                }

                // Cerrar la actividad o realizar otras acciones según sea necesario
                finish();
            }
        });

        seleccionarButton = findViewById(R.id.SelectResources);

        if(editable == false){
            seleccionarButton.setVisibility(View.GONE);
        }else{
            seleccionarButton.setVisibility(View.VISIBLE);
        }

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
        info = ""; //Limpiar la variable info antes de agregarle nuevas opciones

        for (int i = 0; i < options.length; i++) {
            View optionView = optionViews.get(i);
            EditText editText = optionView.findViewById(R.id.dialogEditText);

            if (!editText.getText().toString().isEmpty()) {
                Chip dynamicChip = new Chip(this);
                dynamicChip.setText(options[i] + " | cantidad " + editText.getText().toString());

                // Delimitador "#" al final de cada recurso, sin espacios adicionales
                info += options[i] + " | cantidad " + editText.getText().toString() + "  ";
                dynamicChip.setCloseIconVisible(true);
                dynamicChip.setCheckable(false);
                dynamicChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.getText().clear();
                        updateChipGroup(optionViews);
                    }
                });
                chipGroup.addView(dynamicChip);
            }


    //Nueva instancia de Reservaciones - Guarda las opciones seleccionadas
        Reservaciones reservacion = new Reservaciones();
        reservacion.setRescursos(info);
    }
        //Nueva instancia de Reservaciones - Guarda las opciones seleccionadas
        Reservaciones reservacion = new Reservaciones();
        reservacion.setRescursos(info);


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