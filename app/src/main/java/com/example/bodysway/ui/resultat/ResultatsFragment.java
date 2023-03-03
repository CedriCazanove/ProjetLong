package com.example.bodysway.ui.resultat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodysway.Acceuil;
import com.example.bodysway.Acquisition;
import com.example.bodysway.AcquisitionListAdapter;
import com.example.bodysway.DataBaseHandler;
import com.example.bodysway.DisplayOutcome;
import com.example.bodysway.PatientModule;
import com.example.bodysway.R;
import com.example.bodysway.databinding.FragmentResultatBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResultatsFragment extends Fragment {

    private static final String TAG = "Accelerometer";

    private RecyclerView recyclerView;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog delete_dialog;

    private FragmentResultatBinding binding;

    private File dir;

    private TextView txtView;

    private AcquisitionListAdapter acquisitionListAdapter;

    private ArrayList<Acquisition> acquisitionList = new ArrayList<>();

    private AcquisitionListAdapter.RecyclerViewClickListener listener;

    private PatientModule patientModule;

    private ArrayList<String> patientAllAcquisition;

    private DataBaseHandler db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ResultatsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ResultatsViewModel.class);

        binding = FragmentResultatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle extra = getActivity().getIntent().getExtras();
        int id = extra.getInt("ID");
        patientModule = new PatientModule().getPatientFromID(id, getContext());

        dir = getContext().getFilesDir();
        txtView = binding.txtResultat;
        String ret = "";
        int cntAcquisition = 0;
        patientAllAcquisition = patientModule.getPatientAllAcquisition();
        if (patientAllAcquisition.size() > 0) {
            for (int i = 0; i < patientAllAcquisition.size(); i++) {
                if (!(patientAllAcquisition.get(i).isEmpty())) {
                    ret += patientAllAcquisition.get(i) + "\n";
                    cntAcquisition++;
                    Acquisition acquisition = new Acquisition().getAcquisitionFromFile(patientAllAcquisition.get(i), getContext());
                    acquisitionList.add(acquisition);
                }
            }
        }
        //Toast.makeText(getContext(), "file :\n" + ret, Toast.LENGTH_SHORT).show();

/*
        for (File f : dir.listFiles()) {
            if (f.getName().contains("acquisition")) {
                File fileData = new File(dir, f.getName());
                boolean result = fileData.delete();
                Log.d(TAG, "Clear: " + result);
            }
        }
 */

        Collections.sort(acquisitionList);
        txtView.setText("Number of Acquisition : " + cntAcquisition);

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
                Intent intent = new Intent(getContext(), DisplayOutcome.class);
                intent.putExtra("acquisition_filename", acquisitionList.get(position).getFilename());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View v, int position) {
                dialogBuilder = new AlertDialog.Builder(getContext());
                final View delete_view = getLayoutInflater().inflate(R.layout.popup_delete_file, null);

                Button deleteButton = (Button) delete_view.findViewById(R.id.delete);
                Button cancelButton = (Button) delete_view.findViewById(R.id.annuler);

                dialogBuilder.setView(delete_view);
                delete_dialog = dialogBuilder.create();
                delete_dialog.show();

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File fileData = new File(dir,acquisitionList.get(position).getFilename());
                        boolean result = fileData.delete();
                        Log.d(TAG,"Clear: " + result);
                        if (result) {
                            patientAllAcquisition.remove(acquisitionList.get(position).getFilename());
                            acquisitionList.remove(acquisitionList.get(position));
                            patientModule.setPatientAllAcquisition(patientAllAcquisition);

                            Toast.makeText(getContext(), "Acquisition deleted successfully", Toast.LENGTH_SHORT).show();
                            acquisitionListAdapter.notifyDataSetChanged();

                            txtView.setText("Number of Acquisition : " + patientAllAcquisition.size());
                            db = new DataBaseHandler(getContext());
                            db.updateDB(patientModule);
                            db.close();
                        } else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        delete_dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete_dialog.dismiss();
                    }
                });
                return true;
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}