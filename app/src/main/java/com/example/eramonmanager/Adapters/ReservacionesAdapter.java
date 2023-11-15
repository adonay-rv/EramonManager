package com.example.eramonmanager.Adapters;

import static com.example.eramonmanager.Activity.Recursos.Eliminar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.eramonmanager.Activity.AddReservationActivity;
import com.example.eramonmanager.Activity.Reservaciones;
import com.example.eramonmanager.R;

import java.util.ArrayList;
import java.util.List;

public class ReservacionesAdapter extends RecyclerView.Adapter<ReservacionesAdapter.ReservacionesViewHolder> {
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
        holder.txtAmount.setText("Telefono: " + reservas.getTel());
        holder.txtTimestamp.setText(reservas.getDateReservation());


        holder.itemView.setOnClickListener((v) -> {
            // Crea un nuevo intento que especifica el contexto actual y la clase de destino (AddReservationActivity).
            Intent intent = new Intent(context, AddReservationActivity.class);

            // Obtiene la posición del elemento clicado
            int position1 = holder.getAdapterPosition();

            // Verifica si la posición es válida antes de continuar
            if (position1 != RecyclerView.NO_POSITION) {
                // Obtiene la Reservacion en la posición dada
                Reservaciones reservacion = reservasList.get(position1);


                // Agrega datos adicionales al intento utilizando pares clave-valor.
                intent.putExtra("title", reservacion.getNombre());
                intent.putExtra("dui", reservacion.getDui());
                intent.putExtra("tel", reservacion.getTel());
                intent.putExtra("cantidad", reservacion.getCantidadPersonas());
                intent.putExtra("reservacion", reservacion.getDateReservation());
                intent.putExtra("precio", reservacion.getPrecioReservacion());
                intent.putExtra("salida", reservacion.getFechaSalida());

                // Agrega el ID de la reservación como un extra al intento.
                intent.putExtra("docId", reservacion.getId());
                Log.d("ReservacionAdapter", "Reserva ID: " + position1);
                // Inicia la actividad AddReservationActivity con el intento configurado.
                context.startActivity(intent);
            }
        });


        // Reemplaza getIdDelRecurso con el método adecuado para obtener el ID

        // Botón de eliminación
        holder.pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método Eliminar con el ID del recurso a elimina

                // Ahora aquí, podrías también actualizar la lista o la interfaz de usuario para reflejar el cambio.
            }
        });
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
        public ReservacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.Reservation_Name);
            txtAmount = itemView.findViewById(R.id.Tel_Reservation);
            txtTimestamp = itemView.findViewById(R.id.Timestamp_Reservation);

            pdfButton = itemView.findViewById(R.id.PDF_Reservation);
        }
    }

}
