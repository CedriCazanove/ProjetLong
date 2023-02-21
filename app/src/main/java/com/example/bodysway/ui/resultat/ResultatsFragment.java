package com.example.bodysway.ui.resultat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodysway.Acquisition;
import com.example.bodysway.AcquisitionListAdapter;
import com.example.bodysway.databinding.FragmentResultatBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResultatsFragment extends Fragment {

    private static final String TAG = "Accelerometer";

    private RecyclerView recyclerView;

    private FragmentResultatBinding binding;

    private AcquisitionListAdapter acquisitionListAdapter;

    private ArrayList<Acquisition> acquisitionList = new ArrayList<>();

    private AcquisitionListAdapter.RecyclerViewClickListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ResultatsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ResultatsViewModel.class);

        binding = FragmentResultatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        File dir = getContext().getFilesDir();
        TextView txtView = binding.txtView;
        String ret = "";
        for (File f : dir.listFiles()) {
            ret += f.getName() + "\n";
            Acquisition acquisition = new Acquisition().getAcquisitionFromFile(f.getName(), getContext());
            acquisitionList.add(acquisition);
            /*File fileData = new File(dir,f.getName());
            boolean result = fileData.delete();
            Log.d(TAG,"Clear: " + result);*/
        }
        Collections.sort(acquisitionList);
        txtView.setText("Number of Acquisition : " + dir.listFiles().length);

        recyclerView = binding.outcomeRecyView;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setOnClickListener();

        acquisitionListAdapter = new AcquisitionListAdapter(getContext(), acquisitionList, listener);
        recyclerView.setAdapter(acquisitionListAdapter);

        return root;
    }

    private void setOnClickListener() {
        listener = new AcquisitionListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(getContext(), "Date : " + acquisitionList.get(position).getDateAcquisiton(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        };
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}