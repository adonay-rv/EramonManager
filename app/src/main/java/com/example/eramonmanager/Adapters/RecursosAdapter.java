package com.example.eramonmanager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eramonmanager.Activity.Recursos;
import com.example.eramonmanager.R;

import java.util.List;

public class RecursosAdapter extends RecyclerView.Adapter<RecursosAdapter.RecursosViewHolder> {
    private List<Recursos> recursosList;
    private Context context;

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

        public RecursosViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ListImageResources);
            txtName = itemView.findViewById(R.id.ListNameResource);
            txtAmount = itemView.findViewById(R.id.MountResources);
            txtTimestamp = itemView.findViewById(R.id.Timestamp_Reservation);
        }
    }
}
