package com.example.eramonmanager.Adapters;

import static android.content.ContentValues.TAG;
import static com.example.eramonmanager.Activity.Recursos.Eliminar;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eramonmanager.Activity.Reservaciones;
import android.Manifest;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import com.example.eramonmanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservacionesAdapter extends RecyclerView.Adapter<ReservacionesAdapter.ReservacionesViewHolder> {
    private List<Reservaciones> reservasList;
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
   // private static final int REQUEST_CODE = 1232;

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
        holder.txtAmount.setText("Telefono: " + reservas.getTel());
        holder.txtTimestamp.setText(reservas.getDateReservation());

         // Reemplaza getIdDelRecurso con el método adecuado para obtener el ID

        // Botón para generar pdf
        holder.pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                     //Cuando el permiso no ha sido concedido, debe solicitarse
                     ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                 }else{
                     //askPermissions();
                     //Si el permiso ya ha sido concedido, procede a crearse el archivo pdf
                     GenerarPDF(reservas);
                 }
            }
        });
    }

    //private void askPermissions() {
      //  ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    //}

    private void GenerarPDF(Reservaciones reservaciones){

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
        TextView Elemento8 = view.findViewById(R.id.NumberId);
        TextView Elemento9 = view.findViewById(R.id.CreacionReciboToFirebase);
        TextView Elemento10 = view.findViewById(R.id.total);

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
        }else
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));
        Log.d("my_log", "Width Now" + view.getMeasuredWidth());
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        //Crear una nueva instancia para PdfDocument
        PdfDocument pdfDocument = new PdfDocument();

        //Obtener el width y el height de la vista
        int viewWidth  = view.getMeasuredWidth();
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
        String directoryPath = Environment.getExternalStorageDirectory().toString();
        File file = new File(directoryPath, "Reservas_EramonParadise360" + reservaciones.getNombre() + ".pdf");
        try{
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF generado en: " + file.getPath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            Toast.makeText(context, "Error al generar el PDF" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Cerrar el documento
        pdfDocument.close();
    }

    @Override
    public int getItemCount() {
        return reservasList.size();
    }

    public static class ReservacionesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtName;
        TextView txtAmount;
        TextView txtTimestamp;


        ImageButton pdfButton;
        public ReservacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.Reservation_Name);
            txtAmount = itemView.findViewById(R.id.Tel_Reservation);
            txtTimestamp = itemView.findViewById(R.id.Timestamp_Reservation);

            pdfButton = itemView.findViewById(R.id.PDF_Reservation);
        }
    }

}
