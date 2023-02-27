package com.example.bodysway.ui.profile;

import static android.os.Build.VERSION_CODES.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bodysway.Acceuil;
import com.example.bodysway.Acquisition;
import com.example.bodysway.DataBaseHandler;
import com.example.bodysway.Home;
import com.example.bodysway.PatientModule;
import com.example.bodysway.R;
import com.example.bodysway.databinding.FragmentProfileBinding;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private EditText editFirstName, editLastName, editBirthDate, editDescription;
    private TextView firstNameText, lastNameText, birthDateText, descriptionText;
    private DataBaseHandler db;
    private PatientModule patientModule;
    private Button modifyButton, modifier, annuler;
    private AlertDialog.Builder setModificationDialog;
    private AlertDialog modifyDialog;
    private ArrayList<PatientModule> patientModules;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel homeViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Bundle extra = getActivity().getIntent().getExtras();
        int id = extra.getInt("ID");

        db = new DataBaseHandler(getActivity().getApplicationContext());
        patientModules = db.getAllItems();
        db.close();

        PatientModule patient;

        firstNameText = binding.TextPrenom;
        lastNameText = binding.TextNom;
        birthDateText = binding.TextBirthday;
        descriptionText = binding.TextDescription;
        modifyButton = binding.modify;

        patient = new PatientModule().getPatientFromID(id, getContext());
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setModification(id);
            }
        });

        refreshdata();

        firstNameText.setText(patient.getPatientFistName());
        lastNameText.setText(patient.getPatientLastName());
        birthDateText.setText(patient.getPatientBirthDate());
        descriptionText.setText(patient.getPatientDescription());

        return root;
    }


    public void setModification(int id){
        setModificationDialog = new AlertDialog.Builder(getContext());
        final View modify_view = getLayoutInflater().inflate(com.example.bodysway.R.layout.modify_popup, null);

        setModificationDialog.setView(modify_view);
        modifyDialog = setModificationDialog.create();
        modifyDialog.show();

        editFirstName = modify_view.findViewById(com.example.bodysway.R.id.editTextPrenom);
        editLastName = modify_view.findViewById(com.example.bodysway.R.id.editTextNom);
        editBirthDate = modify_view.findViewById(com.example.bodysway.R.id.editTextBirthday);
        editDescription = modify_view.findViewById(com.example.bodysway.R.id.editTextDescription);
        modifier = modify_view.findViewById(com.example.bodysway.R.id.valider_modif);
        annuler = modify_view.findViewById(com.example.bodysway.R.id.annuler_modif);

        editFirstName.setText(firstNameText.getText());
        editLastName.setText(lastNameText.getText());
        editBirthDate.setText(birthDateText.getText());
        editDescription.setText(descriptionText.getText());

        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Acceuil.class);
                saveModification(id);
                refreshdata();
                modifyDialog.dismiss();
                startActivity(intent);
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyDialog.dismiss();
            }
        });

    }

    public void saveModification(int id){

        //patientModules.clear();

        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String birthDate = editBirthDate.getText().toString();
        String description = editDescription.getText().toString();

        db = new DataBaseHandler(getActivity().getApplicationContext());
        PatientModule patientModule = new PatientModule();

        patientModule.setId(id);
        patientModule.setPatientFirstName(firstName);
        patientModule.setPatientLastName(lastName);
        patientModule.setPatientBirthDate(birthDate);
        patientModule.setPatientDescription(description);

        if (!(firstName.isEmpty()) && !(lastName.isEmpty()) && !(birthDate.isEmpty())){
            db.updateDB(patientModule);
        }
        refreshdata();
        db.close();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void refreshdata() {
        patientModules.clear();

        db = new DataBaseHandler(getActivity().getApplicationContext());
        final ArrayList<PatientModule> itemDataBase = db.getAllItems();
        db.close();

        for (int i = 0; i < itemDataBase.size(); i++) {

            PatientModule patientModule = new PatientModule();

            String firstNameRecorded = itemDataBase.get(i).getPatientFistName();
            String lastNameRecorded = itemDataBase.get(i).getPatientLastName();
            String birthRecorded = itemDataBase.get(i).getPatientBirthDate();
            String descriptionRecorded = itemDataBase.get(i).getPatientDescription();
            int idRecorded = itemDataBase.get(i).getId();

            patientModule.setPatientFirstName(firstNameRecorded);
            patientModule.setPatientLastName(lastNameRecorded);
            patientModule.setPatientBirthDate(birthRecorded);
            patientModule.setPatientDescription(descriptionRecorded);
            patientModule.setId(idRecorded);

            patientModules.add(patientModule);

        }
    }
}