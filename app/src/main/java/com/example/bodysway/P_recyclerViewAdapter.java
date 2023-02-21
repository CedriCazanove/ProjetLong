package com.example.bodysway;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class P_recyclerViewAdapter extends RecyclerView.Adapter<P_recyclerViewAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<PatientModule> patientModules;

    public P_recyclerViewAdapter(Context context, ArrayList<PatientModule> patientModules, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.patientModules = patientModules;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public P_recyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new P_recyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(P_recyclerViewAdapter.MyViewHolder holder, int position) {
        holder.textViewFirstName.setText(patientModules.get(position).getPatientFistName());
        holder.textViewLastName.setText(patientModules.get(position).getPatientLastName());
        holder.textViewDate.setText(patientModules.get(position).getPatientBirthDate());
    }

    @Override
    public int getItemCount() {
        return patientModules.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewFirstName, textViewLastName, textViewDate;
        Button deleteButton;

        public MyViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textViewFirstName = itemView.findViewById(R.id.First_Name);
            textViewLastName = itemView.findViewById(R.id.Last_name);
            textViewDate = itemView.findViewById(R.id.Date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                         int position = getAdapterPosition();

                         if (position != RecyclerView.NO_POSITION) {
                             recyclerViewInterface.onItemClick(position);
                         }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemLongClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }
}
