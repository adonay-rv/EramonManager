package com.example.eramonmanager.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
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
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddReservationActivity extends AppCompatActivity {

    private TextWatcher duiTextWatcher, telTextWatcher;
    private boolean isFormatting = false;
    private ChipGroup chipGroup;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private String imageUrl1;
    private static final int PICK_IMAGE_REQUEST1 = 1;
    private Button imageButton1;
    static TextView pageTitleTextView;
    boolean isEditMode = false;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private boolean isImageUploaded = false;

    EditText titleEditText, teledit, duiedit, cantidadedit, reservacionedit, salidaedit, precoedit;
    private ImageButton seleccionarButton;
    private boolean[] selectedOptions1;
    private String[] options;
    RadioGroup estadoR;

    public String info;

    private Map<String, String> selectedOptions = new HashMap<>();


    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri filePath = result.getData().getData();
                    uploadImage(filePath);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);


        boolean editable = getIntent().getBooleanExtra("editable", true);


        pageTitleTextView = findViewById(R.id.pagetitle);

        titleEditText = findViewById(R.id.Add_Reservation_Name);
        titleEditText.setEnabled(editable);

        duiedit = findViewById(R.id.Add_Reservation_Dui);
        duiedit.setEnabled(editable);

        teledit = findViewById(R.id.Add_Reservation_Tel);
        teledit.setEnabled(editable);

        precoedit = findViewById(R.id.Add_Reservation_ReservationPrice);
        precoedit.setEnabled(editable);

        reservacionedit = findViewById(R.id.Add_Reservation_DateReservation);
        reservacionedit.setEnabled(editable);

        salidaedit = findViewById(R.id.Add_Reservation_DateOut);
        salidaedit.setEnabled(editable);

        cantidadedit = findViewById(R.id.Add_Reservation_AmountPeople);
        cantidadedit.setEnabled(editable);
        Button button1 = findViewById(R.id.Button_AddReservation);

        //receive data
        String title = getIntent().getStringExtra("title");
        String dui = getIntent().getStringExtra("dui");
        String tel = getIntent().getStringExtra("tel");
        String cantidadp = getIntent().getStringExtra("cantidad");
        String precio = getIntent().getStringExtra("precio");
        String fechareservacion = getIntent().getStringExtra("reservacion");
        String fechasalida = getIntent().getStringExtra("salida");
        String docId = getIntent().getStringExtra("docId");
        imageUrl1 = getIntent().getStringExtra("imageUrl");


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

        if (docId != null && !docId.isEmpty()) {
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
        } else if (viewHistorial) {
            pageTitleTextView.setText("Historial");
        } else {
            pageTitleTextView.setText("Editar Reservación");
            button1.setText("Actualizar");
        }

        //Obtener los recursos seleccionados para volver a mostrarlos
        chipGroup = findViewById(R.id.ChipGroupResources);

        String recursos = getIntent().getStringExtra("recursos");
        if (recursos != null) {
            // Utiliza una expresión regular para dividir la cadena de recursos en bloques
            String[] opciones = recursos.split("(?<=\\d)(?=\\D)");

            for (String opcion : opciones) {
                opcion = opcion.trim(); // Elimina los espacios en blanco al principio y al final
                final Chip chip = new Chip(this);
                chip.setText(opcion);
                chip.setClickable(false);

                if (editable) {
                    // Si estás en modo de edición, muestra el ícono de cierre
                    chip.setCloseIconVisible(true);
                    chip.setClickable(true);

                    // Configura el listener para el cierre del Chip
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Elimina el Chip del chipGroup
                            chipGroup.removeView(chip);

                            // También puedes realizar acciones adicionales si es necesario

                            // Actualiza la base de datos u otras operaciones según sea necesario
                            // actualizarRecursos(opcion); // Puedes implementar este método
                        }
                    });
                }

                chipGroup.addView(chip);
            }
        }

        seleccionarButton = findViewById(R.id.SelectResources);
        seleccionarButton.setEnabled(editable);

        options = getResources().getStringArray(R.array.resource_array);
        selectedOptions1 = new boolean[options.length];

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

        if (editable == false) {
            imageButton1.setVisibility(View.GONE);
        } else {
            imageButton1.setVisibility(View.VISIBLE);
        }



        if (editable == false) {
            button1.setVisibility(View.GONE);
        } else {
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
                } if (TextUtils.isEmpty(cantidadPeopleStr)) {
                    showError("El campo Cantidad de personas es requerido");
                    return;
                } else {
                    try {
                        int cantidadPeople = Integer.parseInt(cantidadPeopleStr);
                        if (cantidadPeople <= 0) {
                            showError("La cantidad de personas debe ser mayor que 0");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showError("Ingrese un número válido para la cantidad de personas");
                        return;
                    }
                }

                if (TextUtils.isEmpty(dateReservationStr)) {
                    showError("El campo Fecha de reservación es requerido");
                    return;
                } else if (TextUtils.isEmpty(dateOutStr)) {
                    showError("El campo Fecha de salida es requerido");
                    return;
                } else if (TextUtils.isEmpty(priceStr)) {
                    showError("El campo Precio del paquete es requerido");
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
                            docId, nombreReservacion, duiStr, telStr, cantidadPeopleStr, info,
                            dateReservationStr, dateOutStr, priceStr, estado, imageUrl1);

                    // Limpiar el chipGroup antes de agregar las opciones actualizadas
                    chipGroup.removeAllViews();

                    // Agregar las opciones del chipGroup a selectedOptions
                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) chipGroup.getChildAt(i);
                        String opcion = chip.getText().toString();
                        selectedOptions.put(obtenerNombreRecurso(opcion), obtenerCantidad(opcion));
                    }

                } else {
                    // Modo de creación: Crear una nueva reserva
                    reservaciones.crearReservacion(
                            nombreReservacion, duiStr, telStr, cantidadPeopleStr, info,
                            dateReservationStr, dateOutStr, priceStr, estado, imageUrl1);

                    if (!isImageUploaded) {
                        Toast.makeText(AddReservationActivity.this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                showSuccessMessage("La reserva se ha guardado exitosamente");

                // Cerrar la actividad o realizar otras acciones según sea necesario
                finish();

            }
        });

        seleccionarButton = findViewById(R.id.SelectResources);

        if (editable == false) {
            seleccionarButton.setVisibility(View.GONE);
        } else {
            seleccionarButton.setVisibility(View.VISIBLE);
        }

        selectedOptions1 = new boolean[options.length];

        seleccionarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionSelectionDialog();

            }
        });

        imageButton1.setOnClickListener(view -> checkAndOpenGallery());
        //Eliminar imagen existente
        if (isEditMode) {
            // Establecer un OnClickListener para "Button_UploadPhoto" para manejar la eliminación de la imagen existente
            imageButton1.setOnClickListener(view -> deleteExistingImageAndUploadNewImage());
        } else {
            // Establecer un OnClickListener para "Button_UploadPhoto" para manejar la carga de la nueva imagen
            imageButton1.setOnClickListener(view -> checkAndOpenGallery());
        }


        //Ver imagen
        Button viewPhotoButton = findViewById(R.id.Button_ViewPhoto);

        if (isEditMode == false) {
            //viewPhotoButton.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "PAPITA", Toast.LENGTH_SHORT).show();
            viewPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Muestra la imagen en un Dialog
                    if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                        showImageDialog(imageUrl1);
                    } else {
                        Toast.makeText(AddReservationActivity.this, "No hay imagen disponible", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (viewDetails) {
            viewPhotoButton.setVisibility(View.VISIBLE);
            viewPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadImageDialogInDetailsHistorial();
                }
            });
        } else if (viewHistorial) {
            viewPhotoButton.setVisibility(View.VISIBLE);
            viewPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadImageDialogInDetailsHistorial();
                }
            });
        } else {
            viewPhotoButton.setVisibility(View.VISIBLE);
            viewPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Muestra la imagen en un Dialog
                    if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                        showImageDialog(imageUrl1);
                    } else {
                        Toast.makeText(AddReservationActivity.this, "No hay imagen disponible", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private String obtenerNombreRecurso(String opcion) {
        // Supongamos que el nombre del recurso está antes del guión "-"
        String[] partes = opcion.split("-");
        if (partes.length > 0) {
            return partes[0].trim();
        } else {
            return "";
        }
    }

    private String obtenerCantidad(String opcion) {
        // Supongamos que la cantidad está después del guión "-"
        String[] partes = opcion.split("-");
        if (partes.length > 1) {
            return partes[1].trim();
        } else {
            return "";
        }
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

            // Si la opción está seleccionada previamente, restaurar el valor
            if (selectedOptions.containsKey(options[i])) {
                editText.setText(selectedOptions.get(options[i]));
            }

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
                String updatedInfo = updateChipGroup(optionViews);
                info = updatedInfo; // Actualizar la variable info con la nueva información
            }
        });


        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private String updateChipGroup(List<View> optionViews) {
        int chipCount = chipGroup.getChildCount();
        StringBuilder updatedInfo = new StringBuilder();

        for (int i = 0; i < options.length; i++) {
            View optionView = optionViews.get(i);
            EditText editText = optionView.findViewById(R.id.dialogEditText);
            String currentInput = editText.getText().toString();

            if (!currentInput.isEmpty()) {
                boolean found = false;

                for (int j = 0; j < chipCount; j++) {
                    Chip chip = (Chip) chipGroup.getChildAt(j);
                    String chipText = chip.getText().toString();
                    String resourceName = options[i];

                    if (chipText.contains(resourceName)) {
                        // Actualiza la cantidad sin eliminar el chip
                        chip.setText(resourceName + " -cant " + currentInput);
                        found = true;

                        // Actualiza la opción en el mapa selectedOptions
                        selectedOptions.put(resourceName, currentInput);
                        updatedInfo.append(resourceName).append(" -cant ").append(currentInput).append(" ");

                        break;
                    }
                }

                if (!found) {
                    // Si el chip no existe, crea uno nuevo
                    Chip dynamicChip = new Chip(this);
                    String resourceName = options[i];
                    String formattedOption = resourceName + " -cant " + currentInput;
                    dynamicChip.setText(formattedOption);
                    dynamicChip.setCloseIconVisible(true);
                    dynamicChip.setCheckable(false);

                    // Agrega la nueva opción al mapa selectedOptions
                    selectedOptions.put(resourceName, currentInput);
                    updatedInfo.append(formattedOption).append(" ");

                    final int optionIndex = i;

                    dynamicChip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chipGroup.removeView(dynamicChip);
                            selectedOptions.remove(resourceName);
                        }
                    });

                    chipGroup.addView(dynamicChip);
                }
            }
        }

        // Conserva los recursos existentes que no se actualizaron ni agregaron
        for (int i = 0; i < chipCount; i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            String chipText = chip.getText().toString();

            if (!updatedInfo.toString().contains(chipText)) {
                updatedInfo.append(chipText).append(" ");
            }
        }

        return updatedInfo.toString().trim();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                openGallery();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                manageStoragePermissionLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
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
            String imageName = "imagesComprobantes/" + System.currentTimeMillis() + ".jpg";

            // Mostrar mensaje de subida en proceso
            Toast.makeText(AddReservationActivity.this, "Subiendo imagen...", Toast.LENGTH_SHORT).show();

            storage.getReference().child(imageName).putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de descarga de la imagen subida
                        storage.getReference().child(imageName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    imageUrl1 = uri.toString();
                                    isImageUploaded = true;

                                    // Mostrar mensaje de éxito
                                    Toast.makeText(AddReservationActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();

                                    // Hacer visible el botón después de subir la imagen
                                    Button viewPhotoButton = findViewById(R.id.Button_ViewPhoto);
                                    viewPhotoButton.setVisibility(View.VISIBLE);
                                })
                                .addOnFailureListener(e -> {
                                    // Manejar fallos
                                    Toast.makeText(AddReservationActivity.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Manejar fallos
                        Toast.makeText(AddReservationActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Método para mostrar la imagen en un Dialog
    private void showImageDialog(String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
        ImageView dialogImageView = view.findViewById(R.id.dialogImageView);
        Picasso.get().load(imageUrl).into(dialogImageView);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DownloadImageDialogInDetailsHistorial() {
        String idReserva = getIntent().getStringExtra("docId");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reservaciones").child(idReserva);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reservaciones reservaciones = dataSnapshot.getValue(Reservaciones.class);

                // Obtén la url
                String url = reservaciones.getComprobantePago();

                AlertDialog.Builder builder = new AlertDialog.Builder(AddReservationActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
                ImageView dialogImageView = view.findViewById(R.id.dialogImageView);
                Picasso.get().load(url).into(dialogImageView);

                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void deleteExistingImageAndUploadNewImage() {
        // Eliminar la imagen existente del almacenamiento si la hay
        if (imageUrl1 != null && !imageUrl1.isEmpty()) {
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl1);

            storageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Archivo eliminado exitosamente
                        Toast.makeText(AddReservationActivity.this, "Seleccione una nueva imagen", Toast.LENGTH_SHORT).show();

                        // Subir la nueva imagen
                        checkAndOpenGallery();
                    })
                    .addOnFailureListener(exception -> {
                        // Manejar cualquier error
                        Toast.makeText(AddReservationActivity.this, "Error al eliminar la imagen existente", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Si no hay una imagen existente, simplemente abre la galería para subir una nueva
            checkAndOpenGallery();
        }
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

        // Configurar la fecha mínima
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

        // Configurar la fecha mínima
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

                        // Obtener la fecha y hora de dateReservationEditText
                        String reservationDateTime = dateReservationEditText.getText().toString();
                        Calendar reservationCalendar = parseDateTime(reservationDateTime);

                        // Verificar si la hora seleccionada es futura y al menos una hora después de la hora en dateReservationEditText
                        Calendar minAllowedTime = (Calendar) reservationCalendar.clone();
                        minAllowedTime.add(Calendar.HOUR, 1);

                        if (selectedCalendar.getTimeInMillis() > System.currentTimeMillis() &&
                                (reservationCalendar == null || selectedCalendar.after(minAllowedTime))) {
                            String formattedDateTime = formatDate(year, month, day, hourOfDay, minute);
                            editText.setText(formattedDateTime);
                        } else {
                            // Mostrar un mensaje o realizar alguna acción para indicar que la hora seleccionada no es válida
                            Toast.makeText(getApplicationContext(), "Selecciona una fecha u hora futura", Toast.LENGTH_SHORT).show();
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