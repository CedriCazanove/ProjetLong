package com.example.bodysway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AcquisitionListAdapter extends RecyclerView.Adapter<AcquisitionListAdapter.MyViewHolder> {
    private Context context;

    private ArrayList<Acquisition> list;

    private RecyclerViewClickListener listener;
    public AcquisitionListAdapter(Context context, ArrayList<Acquisition> acquisitionList, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = acquisitionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_outcome, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Acquisition acquisition = list.get(position);
        holder.outcomeDate.setText(acquisition.getDateString() + " (" + acquisition.getRate() + " Hz, " + acquisition.getTime() + "s)");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView outcomeDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            outcomeDate = itemView.findViewById(R.id.outcomeDate);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
        @Override
        public boolean onLongClick(View v) {
            return listener.onLongClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);

        boolean onLongClick(View v, int position);
    }
}
