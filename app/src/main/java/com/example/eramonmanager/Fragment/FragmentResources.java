package com.example.eramonmanager.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.example.eramonmanager.Activity.AddResourceActivity;
import com.example.eramonmanager.Activity.Recursos;
import com.example.eramonmanager.Adapters.RecursosAdapter;
import com.example.eramonmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;





public class FragmentResources extends Fragment {

    FloatingActionButton addRecurso;
    private RecyclerView recyclerView;
    private RecursosAdapter recursosAdapter;
    private List<Recursos> recursosList;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_resources, container, false);


        searchView= (SearchView) rootView.findViewById(R.id.SearchView_Resources);

        recyclerView = rootView.findViewById(R.id.Recyler_View_Resources);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        recursosList = new ArrayList<>();

        recursosAdapter = new RecursosAdapter(recursosList, requireContext());
        recyclerView.setAdapter(recursosAdapter);

        // Obtén los datos de Firebase y llénalos en recursosList

        obtenerDatosDeFirebase();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtra la lista de recursos según el texto de búsqueda
                filter(newText);
                return true;
            }
        });



        return rootView; // Devuelve la vista rootView que configuraste


    }
    private void filter(String text) {
        List<Recursos> filteredList = new ArrayList<>();

        for (Recursos recurso : recursosList) {
            if (recurso.getNombreRecurso().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(recurso);
            }
        }

        recursosAdapter.filterList(filteredList);
    }

    private void obtenerDatosDeFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Recursos");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recursosList.clear(); // Borra los datos anteriores si los hay

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recursos recursos = snapshot.getValue(Recursos.class);
                    if (recursos != null) {
                        recursosList.add(recursos);
                    }
                }

                recursosAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addRecurso = view.findViewById(R.id.Add_Resources);


        addRecurso.setOnClickListener((v) -> {Intent intent = new Intent(requireActivity(), AddResourceActivity.class);
            startActivity(intent);}
        );




    }
}