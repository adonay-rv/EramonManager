package com.example.eramonmanager.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eramonmanager.Activity.AddReservationActivity;
import com.example.eramonmanager.Activity.AddResourceActivity;
import com.example.eramonmanager.Activity.Reservaciones;
import com.example.eramonmanager.Adapters.RecursosAdapter;
import com.example.eramonmanager.Adapters.ReservacionesAdapter;
import com.example.eramonmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentReservations extends Fragment {
    private FloatingActionButton addReservations;
    private RecyclerView recyclerView;
    private ReservacionesAdapter reservacionesAdapter;
    private List<Reservaciones> reservacionesList;
    Button pendientes;

    //Buscador
    SearchView BuscarReservacion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reservations, container, false);
        addReservations = rootView.findViewById(R.id.Add_Reservation);
        recyclerView = rootView.findViewById(R.id.Recyler_View_Reservation);
        pendientes = rootView.findViewById(R.id.Button_Pendientes);

        //Buscador
        BuscarReservacion = rootView.findViewById(R.id.SearchView_Reservation);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reservacionesList = new ArrayList<>();
        reservacionesAdapter = new ReservacionesAdapter(reservacionesList, requireContext());
        recyclerView.setAdapter(reservacionesAdapter);

        // Datos de Firebase en recursosList
        obtenerDatosDeFirebase();

        addReservations.setOnClickListener((v) -> {
            Intent intent = new Intent(requireActivity(), AddReservationActivity.class);
            startActivity(intent);
        });

        pendientes.setOnClickListener(v -> ObtenerReservasPendientes("Pendiente"));
        BuscarReservacion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                BuscarReservacionM(newText);
                return false;
            }
        });

        return rootView;
    }

    //PARA BUSCAR
    void BuscarReservacionM(String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reservaciones");

        // Limpiar la lista de notas antes de agregar las nuevas
        reservacionesList.clear();

        buscarPorCampo(reference.orderByChild("nombre").startAt(searchText).endAt(searchText + "\uf8ff"));
        buscarPorCampo(reference.orderByChild("dateReservation").startAt(searchText).endAt(searchText + "\uf8ff"));

        // searchText a int antes de realizar la consulta
        try {
            int dui = Integer.parseInt(searchText);
            buscarPorCampo(reference.orderByChild("dui").startAt(dui).endAt(dui));
        } catch (NumberFormatException e) {
            // si searchText no es un número, no se realiza la consulta por DUI
        }
    }

    //PARA BUSCAR
    void buscarPorCampo(Query query) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Reservaciones reservaciones = noteSnapshot.getValue(Reservaciones.class);

                    // Agregar la nota a la lista
                    reservacionesList.add(reservaciones);
                }

                // Notificar al adaptador que los datos han cambiado
                reservacionesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void ObtenerReservasPendientes(String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reservaciones");

        // Limpiar la lista de notas antes de agregar las nuevas
        reservacionesList.clear();

        Query query = reference.orderByChild("estado").startAt(searchText).endAt(searchText + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Reservaciones reservaciones = noteSnapshot.getValue(Reservaciones.class);

                    // Agregar la nota a la lista
                    reservacionesList.add(reservaciones);
                }
                // Notificar al adaptador que los datos han cambiado
                reservacionesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void obtenerDatosDeFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Reservaciones");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Reservaciones> nuevasReservacionesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reservaciones reservacion = snapshot.getValue(Reservaciones.class);
                    if (reservacion != null) {
                        nuevasReservacionesList.add(reservacion);
                    }
                }
                reservacionesList.clear();
                reservacionesList.addAll(nuevasReservacionesList);
                reservacionesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo de errores
                Log.e("FirebaseError", "Error al obtener datos de Firebase: " + error.getMessage());
            }
        });
    }
}