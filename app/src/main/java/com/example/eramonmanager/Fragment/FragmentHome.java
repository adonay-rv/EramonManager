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

import java.text.ParseException;
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

        todasLasReservaciones = new ArrayList<>();
        obtenerTodasLasReservaciones();

        // Filtra las reservaciones que coinciden con la fecha actual
        List<Reservaciones> reservacionesFiltradas = filtrarReservacionesPorFecha(todasLasReservaciones, fechaActual);
        actualizaReservaciones(reservacionesFiltradas);


        calendarView_Item.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int Year, int Month, int Day) {
                String FechaSeleccionada = String.format("%02d/%02d/%04d", Day, Month + 1, Year);

                // Filtra las reservaciones que coinciden con la fecha seleccionada
                List<Reservaciones> reservacionesFiltradas = new ArrayList<>();
                SimpleDateFormat formatDateReservation = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (Reservaciones reservacion : todasLasReservaciones) {
                    try {
                        Date date = formatDateReservation.parse(reservacion.getDateReservation());
                        formatDateReservation = new SimpleDateFormat("dd/MM/yyyy");
                        String dateWithoutTime = formatDateReservation.format(date);
                        if (dateWithoutTime.equals(FechaSeleccionada)) {
                            reservacionesFiltradas.add(reservacion);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
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

                textdate.setText(FechaSeleccionada);
            }
        });
        return viewHome;
    }

    public List<Reservaciones> filtrarReservacionesPorFecha(List<Reservaciones> todasLasReservaciones, String fechaActual) {
        List<Reservaciones> reservacionesFiltradas = new ArrayList<>();
        SimpleDateFormat formatDateReservation = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Reservaciones reservacion : todasLasReservaciones) {
            try {
                Date date = formatDateReservation.parse(reservacion.getDateReservation());
                formatDateReservation = new SimpleDateFormat("dd/MM/yyyy");
                String dateWithoutTime = formatDateReservation.format(date);
                if (dateWithoutTime.equals(fechaActual)) {
                    reservacionesFiltradas.add(reservacion);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return reservacionesFiltradas;
    }

    public void actualizaReservaciones(List<Reservaciones> reservaciones) {
        homeAdapter = new HomeAdapter(reservaciones, requireContext());
        recyclerViewReservaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReservaciones.setAdapter(homeAdapter);
    }


    // Este método retorna todas las reservaciones
    public void obtenerTodasLasReservaciones() {

        //Captura la fecha actual
        SimpleDateFormat fecha_fechaActual = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
        String fechaActual = fecha_fechaActual.format(new Date());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reservaciones");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) { // Verifica si el fragmento todavía está adjunto a su actividad
                    todasLasReservaciones.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Reservaciones reservacion = snapshot.getValue(Reservaciones.class);
                        todasLasReservaciones.add(reservacion);
                    }

                    // Filtra las reservaciones que coinciden con la fecha actual
                    List<Reservaciones> reservacionesFiltradas = filtrarReservacionesPorFecha(todasLasReservaciones, fechaActual);

                    actualizaReservaciones(reservacionesFiltradas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (isAdded()) { // Verifica si el fragmento todavía está adjunto a la actividad
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}