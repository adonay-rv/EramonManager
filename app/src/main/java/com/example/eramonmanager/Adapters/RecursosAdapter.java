package com.example.eramonmanager.Adapters;

import static com.example.eramonmanager.Activity.Recursos.Eliminar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eramonmanager.Activity.Recursos;
import com.example.eramonmanager.R;

import java.util.ArrayList;
import java.util.List;

public class RecursosAdapter extends RecyclerView.Adapter<RecursosAdapter.RecursosViewHolder> {
    private List<Recursos> recursosList;
    private Context context;

    // Constructor y otros métodos del adaptador

    // Método para filtrar la lista de recursos
    public void filterList(List<Recursos> filteredList) {
        recursosList = filteredList;
        notifyDataSetChanged();
    }

    public RecursosAdapter(List<Recursos> recursosList, Context context) {
        this.recursosList = recursosList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecursosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resources_list, parent, false);
        return new RecursosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecursosViewHolder holder, int position) {
        Recursos recursos = recursosList.get(position);

        holder.txtName.setText(recursos.getNombreRecurso());
        holder.txtAmount.setText("Disponible: " + recursos.getCantidadRecurso());
        holder.txtTimestamp.setText(recursos.getFechaActualizacion());

        // Utiliza Glide o cualquier otra biblioteca para cargar la imagen en el ImageView
        Glide.with(context)
                .load(recursos.getImagenUrl())
                .centerCrop()
                .into(holder.imageView);

        final String resourceId = recursos.getIdRecursos(); // Reemplaza getIdDelRecurso con el método adecuado para obtener el ID

        // Botón de eliminación
        // Botón de eliminación
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método Eliminar con el ID del recurso a elimina
                Recursos.Eliminar(resourceId, recursos.getImagenUrl());
                // Ahora aquí, podrías también actualizar la lista o la interfaz de usuario para reflejar el cambio.
            }
        });

    }

    @Override
    public int getItemCount() {
        return recursosList.size();
    }

    public static class RecursosViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtName;
        TextView txtAmount;
        TextView txtTimestamp;


        ImageButton deleteButton;
        public RecursosViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ListImageResources);
            txtName = itemView.findViewById(R.id.ListNameResource);
            txtAmount = itemView.findViewById(R.id.MountResources);
            txtTimestamp = itemView.findViewById(R.id.Timestamp_Reservation);

            deleteButton = itemView.findViewById(R.id.Button_Delete);
        }
    }
    public List<String> getNombresYCantidadesRecursos() {
        List<String> nombresYCantidades = new ArrayList<>();
        for (Recursos recurso : recursosList) {
            // Concatena el nombre y la cantidad en un solo String
            String nombreYCantidad = recurso.getNombreRecurso() + " - Disponible: " + recurso.getCantidadRecurso();
            nombresYCantidades.add(nombreYCantidad);
        }
        return nombresYCantidades;
    }
}
