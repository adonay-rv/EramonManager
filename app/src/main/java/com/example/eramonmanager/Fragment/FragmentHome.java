package com.example.eramonmanager.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eramonmanager.Activity.Reservaciones;
import com.example.eramonmanager.Adapters.HomeAdapter;
import com.example.eramonmanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentHome extends Fragment {
    CalendarView calendarView_Item;
    HomeAdapter homeAdapter;
    List<Reservaciones> todasLasReservaciones;
    RecyclerView recyclerViewReservaciones;

    TextView textdate;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewHome = inflater.inflate(R.layout.fragment_home, container, false);

        calendarView_Item = viewHome.findViewById(R.id.calendarView);
        recyclerViewReservaciones = viewHome.findViewById(R.id.recyclerViewReservaciones);
        textdate = viewHome.findViewById(R.id.TextView_Date);

        //Captura la fecha actual
        SimpleDateFormat fecha_fechaActual = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaActual = fecha_fechaActual.format(new Date());
        textdate.setText(fechaActual);

        // Inicializa todasLasReservaciones y homeAdapter
        todasLasReservaciones = new ArrayList<>();
        obtenerTodasLasReservaciones();
        homeAdapter = new HomeAdapter(todasLasReservaciones, requireContext());

        recyclerViewReservaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReservaciones.setAdapter(homeAdapter);

        calendarView_Item.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int Year, int Month, int Day) {
                String FechaSeleccionada = String.format(Day + "/" + (Month + 1) + "/" + Year);

                // Filtra las reservaciones que coinciden con la fecha seleccionada
                List<Reservaciones> reservacionesFiltradas = new ArrayList<>();
                for (Reservaciones reservacion : todasLasReservaciones) {
                    if (reservacion.getDateReservation().equals(FechaSeleccionada)) {
                        reservacionesFiltradas.add(reservacion);
                    }
                }

                // Verifica si hay reservaciones para la fecha seleccionada
                if (reservacionesFiltradas.isEmpty()) {
                    Toast.makeText(getContext(), "No existen reservaciones para la fecha: " + FechaSeleccionada, Toast.LENGTH_SHORT).show();
                } else {
                    // Actualiza el RecyclerView con las reservaciones filtradas
                    homeAdapter.setReservaciones(reservacionesFiltradas);
                    homeAdapter.notifyDataSetChanged();
                }

                //Toast.makeText(getContext(), "Fecha: " + FechaSeleccionada, Toast.LENGTH_SHORT).show();
                textdate.setText(FechaSeleccionada);
            }
        });
        return viewHome;
    }
    
    // Este método retorna todas las reservaciones
    private void obtenerTodasLasReservaciones() {
        // Obtiene una referencia a la base de datos
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtiene una referencia a la colección de reservaciones
        DatabaseReference reservacionesRef = mDatabase.child("Reservaciones");

        // Agrega un ValueEventListener a la referencia de reservaciones
        // Inicializa homeAdapter y configura recyclerViewReservaciones
        homeAdapter = new HomeAdapter(todasLasReservaciones, requireContext());
        recyclerViewReservaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReservaciones.setAdapter(homeAdapter);

        reservacionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Borra la lista de reservaciones para llenarla con los nuevos datos
                todasLasReservaciones.clear();

                // Itera sobre los hijos del DataSnapshot
                for (DataSnapshot reservaSnapshot : dataSnapshot.getChildren()) {
                    // Deserializa el DataSnapshot en un objeto Reservaciones
                    Reservaciones reserva = reservaSnapshot.getValue(Reservaciones.class);

                    // Añade la reservación a la lista
                    todasLasReservaciones.add(reserva);
                }

                // Notifica al adaptador sobre los cambios de datos
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}