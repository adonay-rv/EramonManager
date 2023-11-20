package com.example.eramonmanager.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Locale;
import java.util.Map;

public class AddReservationActivity extends AppCompatActivity {

    private TextWatcher duiTextWatcher, telTextWatcher, priceTextWatcher;
    private boolean isFormatting = false;
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
        String dui = getIntent().getStringExtra("dui");
        String tel = getIntent().getStringExtra("tel");
        String cantidadp = getIntent().getStringExtra("cantidad");
        String precio = getIntent().getStringExtra("precio");
        String fechareservacion = getIntent().getStringExtra("reservacion");
        String fechasalida = getIntent().getStringExtra("salida");
        String docId = getIntent().getStringExtra("docId");


        titleEditText.setText(title);
        teledit.setText(tel);
        duiedit.setText(dui);
        cantidadedit.setText(cantidadp);
        precoedit.setText(precio);
        reservacionedit.setText(fechareservacion);
        salidaedit.setText(fechasalida);

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

        if(title!=null && !title.isEmpty()){
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
            String[] opciones = recursos.split(" ");

            for (int i = 0; i < opciones.length; i += 3) {
                if (i + 2 < opciones.length) {
                    String opcion = opciones[i] + " " + opciones[i+1] + " " + opciones[i+2];
                    Chip chip = new Chip(this);
                    chip.setText(opcion);
                    chip.setClickable(false);
                    chipGroup.addView(chip);
                } else {
                    // Muestra un mensaje al usuario
                    Toast.makeText(this, "Error de formato", Toast.LENGTH_SHORT).show();
                }
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

        //Validacion Dui
        duiTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Formatear el texto al estilo "12345678-9"
                formatDui(editable);
            }
        };

        // Agrega el TextWatcher al campo de teléfono
        duiedit.addTextChangedListener(duiTextWatcher);

        //Validacion Tel
        telTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Formatear el texto al estilo "0000-0000"
                formatTel(editable);
            }
        };

        // Agrega el OnFocusChangeListener al campo de teléfono
        teledit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // Cuando el campo obtiene el foco, agrega el prefijo telefónico
                    String telValue = teledit.getText().toString();
                    if (!telValue.startsWith("+503 ")) {
                        teledit.setText("+503 " + telValue);
                    }
                }
            }
        });

        // Agrega el TextWatcher al campo de teléfono
        teledit.addTextChangedListener(telTextWatcher);

        //Validacion fecha
        EditText dateReservationEditText = findViewById(R.id.Add_Reservation_DateReservation);
        dateReservationEditText.setInputType(InputType.TYPE_NULL);
        showDatePickerDialog(dateReservationEditText);

        EditText dateOutEditText = findViewById(R.id.Add_Reservation_DateOut);
        dateOutEditText.setInputType(InputType.TYPE_NULL);
        showDatePickerDialogWithReservationTimeValidation(dateOutEditText, dateReservationEditText);


        //Validacion precio
        precoedit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        precoedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No es necesario hacer nada antes del cambio de texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No es necesario hacer nada durante el cambio de texto
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isFormatting) {
                    // Aplica el símbolo "$" si aún no está presente
                    String currentText = editable.toString();
                    if (!currentText.startsWith("$")) {
                        isFormatting = true;
                        precoedit.setText("$ " + currentText);
                        precoedit.setSelection(precoedit.getText().length());
                        isFormatting = false;
                    }
                }
            }
        });

        precoedit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // Cuando el campo obtiene el foco, agrega el símbolo "$" si aún no está presente
                    String currentText = precoedit.getText().toString();
                    if (!currentText.startsWith("$")) {
                        precoedit.setText("$ " + currentText);
                    }
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombreReservacionEditText = findViewById(R.id.Add_Reservation_Name);
                EditText duiEditText = findViewById(R.id.Add_Reservation_Dui);
                EditText telEditText = findViewById(R.id.Add_Reservation_Tel);
                EditText cantidadPeopleEditText = findViewById(R.id.Add_Reservation_AmountPeople);
                EditText dateReservationEditText = findViewById(R.id.Add_Reservation_DateReservation);
                EditText dateOutEditText = findViewById(R.id.Add_Reservation_DateOut);
                EditText priceEditText = findViewById(R.id.Add_Reservation_ReservationPrice);

                String nombreReservacion = nombreReservacionEditText.getText().toString();
                String duiStr = duiEditText.getText().toString();
                String telStr = telEditText.getText().toString();
                String cantidadPeopleStr = cantidadPeopleEditText.getText().toString();
                String dateReservationStr = dateReservationEditText.getText().toString();
                String dateOutStr = dateOutEditText.getText().toString();
                String priceStr = priceEditText.getText().toString();

                // Validar que todos los campos estén llenos
                if (TextUtils.isEmpty(nombreReservacion)) {
                    showError("El nombre de la reserva es requerido");
                    return; // Detener la ejecución si la validación falla
                } else if (TextUtils.isEmpty(duiStr)) {
                    showError("El campo Dui es requerido");
                    return;
                } else if (TextUtils.isEmpty(telStr)) {
                    showError("El campo Teléfono es requerido");
                    return;
                } else if (TextUtils.isEmpty(cantidadPeopleStr)) {
                    showError("El campo Cantidad de personas es requerido");
                    return;
                } else if (TextUtils.isEmpty(dateReservationStr)) {
                    showError("El campo Fecha de reservación es requerido");
                    return;
                } else if (TextUtils.isEmpty(dateOutStr)) {
                    showError("El campo Fecha de salida es requerido");
                    return;
                } else if (TextUtils.isEmpty(priceStr)) {
                    showError("El campo Precio del paquete es requerido");
                    return;
                }else if (TextUtils.isEmpty(imageUrl1)) {
                    showError("Debes subir el comprobante de pago");
                    return;
                }

                // Validar que al menos un recurso haya sido seleccionado
                if (chipGroup.getChildCount() == 0) {
                    showError("Debes seleccionar al menos un recurso");
                    return;
                }

                // Validar que se haya seleccionado una opción en el RadioGroup
                int radioButtonId = estadoR.getCheckedRadioButtonId();
                if (radioButtonId == -1) {
                    showError("Debes seleccionar un estado (Cancelado o Pendiente)");
                    return;
                }

                //Validaciones de formato
                if (!isValidDuiFormat(duiStr)) { // Validar el formato del número de dui
                    showError("El formato del Dui no es válido");
                    return;
                } else if (!isValidTelFormat(telStr)) { // Validar el formato del número de teléfono
                    showError("El formato del número de teléfono no es válido");
                    return;
                }

                String idReservacion = mDatabase.child("Reservaciones").push().getKey();
                RadioGroup radioGroup = findViewById(R.id.radioGroupStatus);
                String estado = obtenerEstadoPorRadioButtonId(radioButtonId);

                Reservaciones reservaciones = new Reservaciones();
                if (isEditMode) {

                    // Modo de edición: Actualizar la reserva existente
                    reservaciones.actualizarReserva(
                            title, nombreReservacion, duiStr, telStr, cantidadPeopleStr, info,
                            dateReservationStr, dateOutStr, priceStr, estado, imageUrl1);
                } else {
                    // Modo de creación: Crear una nueva reserva
                    reservaciones.crearReservacion(
                             nombreReservacion, duiStr, telStr, cantidadPeopleStr, info,
                            dateReservationStr, dateOutStr, priceStr, estado, imageUrl1);
                }

                showSuccessMessage("La reserva se ha guardado exitosamente");

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
                dynamicChip.setText(options[i] + " -cant " + editText.getText().toString());
                info += options[i] + " -cant " + editText.getText().toString() + " ";
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

    //Formato de Dui
    private void formatDui(Editable editable) {
        String dui = editable.toString();

        // Eliminar caracteres no numéricos
        dui = dui.replaceAll("[^0-9]", "");

        // Aplicar el formato "12345678-9"
        if (dui.length() > 8) {
            String formattedDui = dui.substring(0, 8) + "-" + dui.substring(8);

            // Remover el TextWatcher temporalmente para evitar bucles infinitos
            duiedit.removeTextChangedListener(duiTextWatcher);

            // Limitar la longitud total del texto a 10 caracteres
            formattedDui = formattedDui.substring(0, Math.min(formattedDui.length(), 10));

            duiedit.setText(formattedDui);
            duiedit.setSelection(formattedDui.length());

            // Volver a agregar el TextWatcher
            duiedit.addTextChangedListener(duiTextWatcher);
        }
    }

    // Función para validar el formato del Dui
    private boolean isValidDuiFormat(String dui) {
        // El formato esperado es "12345678-9"
        // Puedes personalizar esta expresión regular según tus requisitos
        String duiRegex = "\\d{8}-\\d{1}";
        return dui.matches(duiRegex);
    }

    //Formato de Telefono
    private void formatTel(Editable editable) {
        String tel = editable.toString();

        tel = tel.replaceAll("[^+0-9]", "");

        // Aplicar el formato "+503 1234 5678"
        if (tel.length() > 3) {
            String formattedTel;
            if (tel.length() <= 7) {
                formattedTel = tel.substring(0, 4) + " " + tel.substring(4);
            } else {
                formattedTel = tel.substring(0, 4) + " " + tel.substring(4, 8) + " " + tel.substring(8);
            }

            // Remover el TextWatcher temporalmente para evitar bucles infinitos
            teledit.removeTextChangedListener(telTextWatcher);

            // Limitar la longitud total del texto a 14 caracteres (incluyendo los espacios)
            formattedTel = formattedTel.substring(0, Math.min(formattedTel.length(), 14));

            teledit.setText(formattedTel);
            teledit.setSelection(formattedTel.length());

            // Volver a agregar el TextWatcher
            teledit.addTextChangedListener(telTextWatcher);
        }
    }

    // Función para validar el formato del número de teléfono
    private boolean isValidTelFormat(String tel) {
        // El formato esperado es "+503 1234 5678"
        // Puedes personalizar esta expresión regular según tus requisitos
        String telRegex = "\\+503 \\d{4} \\d{4}";
        return tel.matches(telRegex);
    }


    //Para la validacion de la fecha
    private void showDatePickerDialog(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showTimePickerDialog(editText, year, monthOfYear, dayOfMonth, 0, 0);
                    }
                },
                year, month, day
        );

        // Configurar la fecha mínima (opcional, ajusta según tus necesidades)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Asegurarse de que el teclado virtual esté cerrado
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    // Mostrar el DatePickerDialog
                    datePickerDialog.show();
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Asegurarse de que el teclado virtual esté cerrado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                // Mostrar el DatePickerDialog
                datePickerDialog.show();
            }
        });
    }

    private void showTimePickerDialog(final EditText editText, final int year, final int month, final int day, final int hourReservation, final int minuteReservation) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        final Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, day, hourOfDay, minute);

                        // Verificar si la hora seleccionada es futura
                        if (selectedCalendar.getTimeInMillis() > System.currentTimeMillis()) {
                            String formattedDateTime = formatDate(year, month, day, hourOfDay, minute);
                            editText.setText(formattedDateTime);
                        } else {
                            // Mostrar un mensaje o realizar alguna acción para indicar que la hora seleccionada no es válida
                            Toast.makeText(getApplicationContext(), "Selecciona una hora futura", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private void showDatePickerDialogWithReservationTimeValidation(final EditText editText, final EditText dateReservationEditText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showTimePickerDialogWithReservationValidation(editText, year, monthOfYear, dayOfMonth, dateReservationEditText);
                    }
                },
                year, month, day
        );

        // Configurar la fecha mínima (opcional, ajusta según tus necesidades)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Asegurarse de que el teclado virtual esté cerrado
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    // Mostrar el DatePickerDialog
                    datePickerDialog.show();
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Asegurarse de que el teclado virtual esté cerrado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                // Mostrar el DatePickerDialog
                datePickerDialog.show();
            }
        });
    }

    private void showTimePickerDialogWithReservationValidation(final EditText editText, final int year, final int month, final int day, final EditText dateReservationEditText) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        final Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, day, hourOfDay, minute);

                        // Obtener la hora de dateReservationEditText
                        String reservationDateTime = dateReservationEditText.getText().toString();
                        Calendar reservationCalendar = parseDateTime(reservationDateTime);

                        // Verificar si la hora seleccionada es futura y diferente de la hora de dateReservationEditText
                        if (selectedCalendar.getTimeInMillis() > System.currentTimeMillis() &&
                                (reservationCalendar == null || !isSameTime(selectedCalendar, reservationCalendar))) {
                            String formattedDateTime = formatDate(year, month, day, hourOfDay, minute);
                            editText.setText(formattedDateTime);
                        } else {
                            // Mostrar un mensaje o realizar alguna acción para indicar que la hora seleccionada no es válida
                            Toast.makeText(getApplicationContext(), "Selecciona fecha o hora futura", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private boolean isSameTime(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
                calendar1.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY) &&
                calendar1.get(Calendar.MINUTE) == calendar2.get(Calendar.MINUTE);
    }

    private Calendar parseDateTime(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(dateTime));
            return calendar;
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    //mensaje de error
    private void showError(String errorMessage) {
        Toast.makeText(AddReservationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Función para mostrar un mensaje de éxito
    private void showSuccessMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}