package com.example.eramonmanager.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.eramonmanager.Activity.AddReservationActivity;
import com.example.eramonmanager.Activity.Reservaciones;
import com.example.eramonmanager.R;

import java.util.List;


public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {
    private List<Reservaciones> reservasList;
    private Context context;

    public HistorialAdapter(List<Reservaciones> reservasList, Context context) {
        this.reservasList = reservasList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistorialAdapter.HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_list, parent, false);
        return new HistorialAdapter.HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Reservaciones reservas = reservasList.get(position);

        holder.txtName.setText(reservas.getNombre());
        holder.txtAmount.setText("Telefono: " + reservas.getTel());
        holder.txtTimestamp.setText(reservas.getDateReservation());

        holder.itemView.setOnClickListener((v) -> {
            // Crea un nuevo intento que especifica el contexto actual y la clase de destino (AddReservationActivity).
            Intent intent = new Intent(context, AddReservationActivity.class);

            // Obtiene la posición del elemento clicado
            int positions = holder.getAdapterPosition();

            // Verifica si la posición es válida antes de continuar
            if (positions != RecyclerView.NO_POSITION) {
                // Obtiene la Reservacion en la posición dada

                // Agrega datos adicionales al intento utilizando pares clave-valor.
                intent.putExtra("viewHistorial", true);
                intent.putExtra("title", reservas.getNombre());
                intent.putExtra("dui", reservas.getDui());
                intent.putExtra("tel", reservas.getTel());
                intent.putExtra("cantidad", reservas.getCantidadPersonas());
                intent.putExtra("reservacion", reservas.getDateReservation());
                intent.putExtra("precio", reservas.getPrecioReservacion());
                intent.putExtra("salida", reservas.getFechaSalida());
                intent.putExtra("recursos", reservas.getRescursos());
                intent.putExtra("estado", reservas.getEstado());
                intent.putExtra("editable", false);
                intent.putExtra("viewHistorial", true);
                // Agrega el ID de la reservación como un extra al intento.
                intent.putExtra("docId", reservas.getId());

                // Muestra el ID de la reserva en los logs
                Log.d("ReservacionAdapter", "Reserva ID: " + reservas.getId());

                //holder.button_AddReservation.setVisibility(View.GONE);
                //holder.detalles.setText("Detalles de reservacion");

                // Inicia la actividad AddReservationActivity con el intento configurado.
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservasList.size();
    }

    // Método para realizar el filtrado por nombre de la persona y fecha
    public void filterList(List<Reservaciones> filteredList) {
        reservasList.clear();
        reservasList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtAmount;
        TextView txtTimestamp;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.Reservation_Name_Historial);
            txtAmount = itemView.findViewById(R.id.Tel_Reservation_Historial);
            txtTimestamp = itemView.findViewById(R.id.Timestamp_Reservation_Historial);
        }
    }
}
