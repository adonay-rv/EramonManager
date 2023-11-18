package com.example.eramonmanager.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eramonmanager.Activity.Reservaciones;
import com.example.eramonmanager.Adapters.HistorialAdapter;
import com.example.eramonmanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FragmentHistorial extends Fragment {
    private RecyclerView recyclerView;
    private HistorialAdapter historialAdapter;
    private List<Reservaciones> reservacionesList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historial, container, false);

        recyclerView = rootView.findViewById(R.id.Recyler_View_Historial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservacionesList = new ArrayList<>();
        historialAdapter = new HistorialAdapter(reservacionesList, requireContext());
        recyclerView.setAdapter(historialAdapter);

        // Obtén los datos de Firebase y llénalos en recursosList
        obtenerDatosDeFirebase();



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
                historialAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo de errores
                Log.e("FirebaseError", "Error al obtener datos de Firebase: " + error.getMessage());
            }
        });
    }
}