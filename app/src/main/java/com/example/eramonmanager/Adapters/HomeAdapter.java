package com.example.eramonmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eramonmanager.Activity.AddReservationActivity;
import com.example.eramonmanager.Activity.Reservaciones;
import com.example.eramonmanager.Fragment.FragmentHome;
import com.example.eramonmanager.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    Context context;
    private List<Reservaciones> reservacionesList;

    public HomeAdapter(List<Reservaciones> reservaDBList, Context context) {
        this.reservacionesList = reservaDBList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservations_calendar, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Reservaciones reservaciones = reservacionesList.get(position);

        holder.txtNameCalendar.setText(reservaciones.getNombre());
        holder.txtDateCalendar.setText(reservaciones.getDateReservation());
       // holder.Button_AddReservation.setVisibility(View.GONE);

        holder.itemView.setOnClickListener((v) -> {
            // Crea un nuevo intento que especifica el contexto actual y la clase de destino (AddReservationActivity).
            Intent intent = new Intent(context, AddReservationActivity.class);

            // Obtiene la posición del elemento clicado
            int positions = holder.getAdapterPosition();

            // Verifica si la posición es válida antes de continuar
            if (positions != RecyclerView.NO_POSITION) {
                // Obtiene la Reservacion en la posición dada
                Reservaciones reservacion = reservacionesList.get(positions);

                // Agrega datos adicionales al intento utilizando pares clave-valor.
                intent.putExtra("title", reservacion.getNombre());
                intent.putExtra("dui", reservacion.getDui());
                intent.putExtra("tel", reservacion.getTel());
                intent.putExtra("cantidad", reservacion.getCantidadPersonas());
                intent.putExtra("reservacion", reservacion.getDateReservation());
                intent.putExtra("precio", reservacion.getPrecioReservacion());
                intent.putExtra("salida", reservacion.getFechaSalida());
                intent.putExtra("recursos", reservacion.getRescursos());
                intent.putExtra("estado", reservacion.getEstado());
                intent.putExtra("editable", false);
                // Agrega el ID de la reservación como un extra al intento.
                intent.putExtra("docId", reservacion.getId());

                // Muestra el ID de la reserva en los logs
                Log.d("ReservacionAdapter", "Reserva ID: " + reservacion.getId());

                //holder.button_AddReservation.setVisibility(View.GONE);
                //holder.detalles.setText("Detalles de reservacion");

                // Inicia la actividad AddReservationActivity con el intento configurado.
                context.startActivity(intent);
            }
        });
    }

    public void setReservaciones(List<Reservaciones> nuevasReservaciones) {
        this.reservacionesList = nuevasReservaciones;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reservacionesList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameCalendar;
        TextView txtDateCalendar;
        //Button button_AddReservation;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameCalendar = itemView.findViewById(R.id.reservation_name_calendar);
            txtDateCalendar = itemView.findViewById(R.id.reservation_date_calendar);
            //detalles = itemView.findViewById(R.id.pagetitle);
            //button_AddReservation = itemView.findViewById(R.id.Button_AddReservation);
        }
    }
}
