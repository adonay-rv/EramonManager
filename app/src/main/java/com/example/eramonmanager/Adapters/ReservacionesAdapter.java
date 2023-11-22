package com.example.eramonmanager.Adapters;

import static com.example.eramonmanager.Activity.Recursos.Eliminar;
import static com.example.eramonmanager.Activity.Reservaciones.EliminarR;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.eramonmanager.Activity.AddReservationActivity;
import com.example.eramonmanager.Activity.Reservaciones;
import android.Manifest;
import com.example.eramonmanager.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Enlazar datos de las reservaciones con el recyclerview
public class ReservacionesAdapter extends RecyclerView.Adapter<ReservacionesAdapter.ReservacionesViewHolder> {
    //Permiso de escritura en el almacenamiento externo
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    //Permiso de administracion del almacenamiento externo
    private static final int MY_PERMISSIONS_REQUEST_MANAGE_EXTERNAL_STORAGE = 2;
    //Datos que el adapter utilizara para llenar el RecyclerView
    private List<Reservaciones> reservasList;
    private Context context;

    public ReservacionesAdapter(List<Reservaciones> reservasList, Context context) {
        this.reservasList = reservasList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReservacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservations_list, parent, false);
        return new ReservacionesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReservacionesViewHolder holder, int position) {
        Reservaciones reservas = reservasList.get(position);

        holder.txtName.setText(reservas.getNombre());
        holder.txtAmount.setText("Teléfono: " + reservas.getTel());
        holder.txtTimestamp.setText(reservas.getDateReservation());


        holder.itemView.setOnClickListener((v) -> {
            // Crea un nuevo intento que especifica el contexto actual y la clase de destino (AddReservationActivity).
            Intent intent = new Intent(context, AddReservationActivity.class);

            // Obtiene la posición del elemento clicado
            int positions = holder.getAdapterPosition();

            // Verifica si la posición es válida antes de continuar
            if (positions != RecyclerView.NO_POSITION) {
                // Obtiene la Reservacion en la posición dada
                Reservaciones reservacion = reservasList.get(positions);

                // Agrega datos adicionales al intento utilizando pares clave-valor.
                intent.putExtra("title", reservacion.getNombre());
                intent.putExtra("dui", reservacion.getDui());
                intent.putExtra("tel", reservacion.getTel());
                intent.putExtra("cantidad", reservacion.getCantidadPersonas());
                intent.putExtra("reservacion", reservacion.getDateReservation());
                intent.putExtra("precio", reservacion.getPrecioReservacion());
                intent.putExtra("salida", reservacion.getFechaSalida());
                intent.putExtra("estado", reservacion.getEstado());
                intent.putExtra("recursos", reservacion.getRescursos());
                // Agrega el ID de la reservación como un extra al intento.
                intent.putExtra("docId", reservacion.getId());

                // Muestra el ID de la reserva en los logs
                Log.d("ReservacionAdapter", "Reserva ID: " + reservacion.getId());

                // Inicia la actividad AddReservationActivity con el intento configurado.
                context.startActivity(intent);
            }
        });

        final String resourceId = reservas.getId();
        holder.deleteButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EliminarR(resourceId, reservas.getComprobantePago());

                Toast.makeText(context, "Reservación eliminada correctamente", Toast.LENGTH_SHORT).show();
            }

        });


        // Botón de generar pdf
        holder.pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprueba la version del sistema operativo, si es android 11 o superior
                //Verfifica si tiene el permiso para administrar el almacenamiento externo
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    //verifica si la app tiene permiso en android 11 o superior
                    if (Environment.isExternalStorageManager()) {
                        try {
                            GenerarPDF(reservas);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //verifica que se obtenga el permiso si aun no se tiene
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        ((Activity) context).startActivityForResult(intent, MY_PERMISSIONS_REQUEST_MANAGE_EXTERNAL_STORAGE);
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //verifican si se tiene permiso en android 6.0 o superior, pero inferior a android 11
                        try {
                            GenerarPDF(reservas);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //solicita permiso de escritura
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    try {
                        //Genera el pdf
                        GenerarPDF(reservas);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void GenerarPDF(Reservaciones reservaciones) throws IOException {

        //Se infla el layout XML
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_reserva, null);

        //Capturar la fecha actual
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaHoy = simpleDateFormat.format(new Date());

        //Se inicializan las variables dentro del archivo XML
        TextView Elemento1 = view.findViewById(R.id.FechaActual);
        TextView Elemento2 = view.findViewById(R.id.Cant_Personas);
        TextView Elemento3 = view.findViewById(R.id.FechaEntrada);
        TextView Elemento4 = view.findViewById(R.id.FechaSalida);
        TextView Elemento5 = view.findViewById(R.id.ServiciosEramon);
        TextView Elemento6 = view.findViewById(R.id.NombreCliente);
        TextView Elemento7 = view.findViewById(R.id.TelCliente);
        TextView Elemento8 = view.findViewById(R.id.NumberDui);
        TextView Elemento9 = view.findViewById(R.id.Id_ReservaPdf);
        TextView Elemento10 = view.findViewById(R.id.Total);

        //Pasamos los datos a cada elemento, para que se muestre en el pdf segun reservacion
        Elemento1.setText(fechaHoy);
        Elemento2.setText(String.valueOf(reservaciones.getCantidadPersonas()));
        Elemento3.setText(reservaciones.getDateReservation());
        Elemento4.setText(reservaciones.getFechaSalida());
        Elemento5.setText(reservaciones.getRescursos());
        Elemento6.setText(reservaciones.getNombre());
        Elemento7.setText(String.valueOf(reservaciones.getTel()));
        Elemento8.setText(String.valueOf(reservaciones.getDui()));
        Elemento9.setText(reservaciones.getId());
        Elemento10.setText(String.valueOf(reservaciones.getPrecioReservacion()));

        DisplayMetrics displayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getDisplay().getRealMetrics(displayMetrics);
        } else
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));
        Log.d("my_log", "Width Now" + view.getMeasuredWidth());
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        //Crear una nueva instancia para PdfDocument
        PdfDocument pdfDocument = new PdfDocument();

        //Obtener el width y el height de la vista
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();

        //Crear un PageInfo
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create();

        //Crear una nueva page
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        //Crer un objeto Canvas para dibujar sobre la pagina
        Canvas canvas = page.getCanvas();

        //Crear un objeto Paint para estilizar la vista
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        //Dibujar sobre la vista el canvas
        view.draw(canvas);

        //Finalizar la pagina
        pdfDocument.finishPage(page);

        //Guardar el documento en un archivo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Verifica la version del sistema operativo y ejecuta bloques de codigo segun

            // Para Android 10 y superior
            //Crea duplicados
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "Reservas_EramonParadise360" + reservaciones.getNombre() + ".pdf");
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            //Inserta el archivo en el almacenamiento del dispositivo

            //Abre el OutputStream
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            outputStream.close();

            //Ruta del archivo generado
            Toast.makeText(context, "PDF generado en: " + uri.getPath(), Toast.LENGTH_SHORT).show();
        } else {

            // Para Android 9 y versiones anteriores
            //Sobreescribe documentos
            //Obtiene la ruta del directorio de descarga
            String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(directoryPath, "Reservas_EramonParadise360" + reservaciones.getNombre() + ".pdf");
            pdfDocument.writeTo(new FileOutputStream(file));
            //Crea un nuevo objeto file

            Toast.makeText(context, "PDF generado en: " + file.getPath(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        return reservasList.size();
    }
    public Reservaciones getItem(int position) {
        return reservasList.get(position);
    }
    public static class ReservacionesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtName;
        TextView txtAmount;
        TextView txtTimestamp;
        ImageButton pdfButton;
        ImageButton deleteButton1;
        public ReservacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.Reservation_Name);
            txtAmount = itemView.findViewById(R.id.Tel_Reservation);
            txtTimestamp = itemView.findViewById(R.id.Timestamp_Reservation);

            pdfButton = itemView.findViewById(R.id.PDF_Reservation);
            deleteButton1 = itemView.findViewById(R.id.Button_Delete_R);
        }
    }

}