package com.example.eramonmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FragmentRecursos extends Fragment {

    FloatingActionButton addRecurso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recursos, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addRecurso = view.findViewById(R.id.Add_Recurso);

        addRecurso.setOnClickListener((v) -> {Intent intent = new Intent(requireActivity(), AddRecurso.class);
            startActivity(intent);}
        );
    }
}