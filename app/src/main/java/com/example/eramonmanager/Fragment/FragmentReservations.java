package com.example.eramonmanager.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eramonmanager.Activity.AddReservationActivity;
import com.example.eramonmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentReservations extends Fragment {

    FloatingActionButton addReservations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addReservations = view.findViewById(R.id.Add_Reservation);

        addReservations.setOnClickListener((v) -> {
            Intent intent = new Intent(requireActivity(), AddReservationActivity.class);
            startActivity(intent);}
        );
    }
}