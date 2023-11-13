package com.example.eramonmanager.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentReservations extends Fragment {

    private FloatingActionButton addReservations;
    private FloatingActionButton addRecurso;
    private RecyclerView recyclerView;
    private ReservacionesAdapter reservacionesAdapter;
    private List<Reservaciones> reservacionesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reservations, container, false);

        addReservations = rootView.findViewById(R.id.Add_Reservation);

        recyclerView = rootView.findViewById(R.id.Recyler_View_Reservation);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservacionesList = new ArrayList<>();
        reservacionesAdapter = new ReservacionesAdapter(reservacionesList, requireContext());
        recyclerView.setAdapter(reservacionesAdapter);

        // Obtén los datos de Firebase y llénalos en recursosList
        obtenerDatosDeFirebase();

        addReservations.setOnClickListener((v) -> {
            Intent intent = new Intent(requireActivity(), AddReservationActivity.class);
            startActivity(intent);
        });

        return rootView; // Devuelve la vista rootView que configuraste
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
