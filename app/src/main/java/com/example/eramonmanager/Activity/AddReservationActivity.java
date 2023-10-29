package com.example.eramonmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eramonmanager.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AddReservationActivity extends AppCompatActivity {


    private ChipGroup chipGroup;
    private ImageButton seleccionarButton;
    private boolean[] selectedOptions;
    private String[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        chipGroup = findViewById(R.id.ChipGroupResources);
        seleccionarButton = findViewById(R.id.SelectResources);

        options = getResources().getStringArray(R.array.resource_array);
        selectedOptions = new boolean[options.length];

        seleccionarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionSelectionDialog();
            }
        });
    }

    private void showOptionSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona las opciones");
        builder.setMultiChoiceItems(options, selectedOptions, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedOptions[which] = isChecked;
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateChipGroup();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void updateChipGroup() {
        chipGroup.removeAllViews();
        for (int i = 0; i < options.length; i++) {
            if (selectedOptions[i]) {
                Chip chip = new Chip(this);
                chip.setText(options[i]);
                chip.setCloseIconVisible(true);
                chip.setCheckable(false);
                final int position = i;
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedOptions[position] = false;
                        updateChipGroup();
                    }
                });
                chipGroup.addView(chip);
            }
        }
    }
}
