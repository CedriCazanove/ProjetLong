package com.example.bodysway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodysway.Acquisition;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
        holder.outcomeDate.setText(acquisition.getDateString() + " at " + acquisition.getRate() + " Hz and during " + acquisition.getTime() + "s");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView outcomeDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            outcomeDate = itemView.findViewById(R.id.outcomeDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);

        void onLongClick(View v, int position);
    }
}
