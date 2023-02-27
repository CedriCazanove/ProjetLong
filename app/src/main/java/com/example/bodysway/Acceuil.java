package com.example.bodysway;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class Acceuil extends AppCompatActivity implements RecyclerViewInterface{

    private static final String TAG = "Accelerometer";

    ArrayList<PatientModule> patientModules = new ArrayList<>();
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private AlertDialog delete_dialog;
    private AlertDialog error_dialog;
    private EditText popUp_patientFirstName, popUp_patientLastName, popUp_patientBirthDate;
    private Button popUp_valider, popUp_annuler, deleteButton, cancelButton, errorButton;
    private DataBaseHandler db;
    private RecyclerView recyclerView;
    private P_recyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        recyclerView = findViewById(R.id.mRecyclerView);

        adapter = new P_recyclerViewAdapter(this, patientModules, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setPatientModules();

        FloatingActionButton button = findViewById(R.id.floatingActionButton4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewContactDialog();
            }
        });

        refreshdata();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setPatientModules() {
        patientModules.clear();

        db = new DataBaseHandler(getApplicationContext());
        final ArrayList<PatientModule> itemDataBase = db.getAllItems();


        for (int i = 0; i < itemDataBase.size(); i++) {

            PatientModule patientModule = new PatientModule();

            String firstNameRecorded = itemDataBase.get(i).getPatientFistName();
            String lastNameRecorded = itemDataBase.get(i).getPatientLastName();
            String birthRecorded = itemDataBase.get(i).getPatientBirthDate();
            String descriptionRecorded = itemDataBase.get(i).getPatientDescription();
            ArrayList<String> acquisitionRecorded = itemDataBase.get(i).getPatientAllAcquisition();
            int idRecorded = itemDataBase.get(i).getId();

            patientModule.setPatientFirstName(firstNameRecorded);
            patientModule.setPatientLastName(lastNameRecorded);
            patientModule.setPatientBirthDate(birthRecorded);
            patientModule.setPatientDescription(descriptionRecorded);
            patientModule.setPatientAllAcquisition(acquisitionRecorded);
            patientModule.setId(idRecorded);

            patientModules.add(patientModule);
        }
        db.close();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Acceuil.this, Home.class);

        int id = patientModules.get(position).getId();
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View delete_view = getLayoutInflater().inflate(R.layout.popup_delete, null);

        deleteButton = (Button) delete_view.findViewById(R.id.delete);
        cancelButton = (Button) delete_view.findViewById(R.id.annuler);

        dialogBuilder.setView(delete_view);
        delete_dialog = dialogBuilder.create();
        delete_dialog.show();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = new DataBaseHandler(getApplicationContext());
                int rowPosition = patientModules.get(position).getId();

                File dir = getFilesDir();
                ArrayList<String> patientAllAcquisition = patientModules.get(position).getPatientAllAcquisition();
                for(int i = 0; i < patientAllAcquisition.size(); i++) {
                    File fileData = new File(dir, patientAllAcquisition.get(i));
                    boolean result = fileData.delete();
                    Log.d(TAG, "Clear: " + result);
                }

                db.deleteItem(rowPosition);
                refreshdata();
                db.close();

                delete_dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_dialog.dismiss();
            }
        });
    }

    public void createNewContactDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popUp_view = getLayoutInflater().inflate(R.layout.pop_up, null);

        popUp_patientFirstName = (EditText) popUp_view.findViewById(R.id.popUp_FirstName);
        popUp_patientLastName = (EditText) popUp_view.findViewById(R.id.popUp_LastName);
        popUp_patientBirthDate = (EditText) popUp_view.findViewById(R.id.popUp_BirthDate);

        popUp_valider = (Button) popUp_view.findViewById(R.id.savebutton);
        popUp_annuler = (Button) popUp_view.findViewById(R.id.cancelbutton);

        dialogBuilder.setView(popUp_view);
        dialog = dialogBuilder.create();
        dialog.show();

        popUp_valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDataBase();
                refreshdata();
                dialog.dismiss();
            }
        });

        popUp_annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void saveToDataBase(){
        db = new DataBaseHandler(getApplicationContext());
        PatientModule item = new PatientModule();

        String firstName = popUp_patientFirstName.getText().toString();
        String lastName = popUp_patientLastName.getText().toString();
        String birth = popUp_patientBirthDate.getText().toString();

        if (!(firstName.isEmpty()) && !(lastName.isEmpty()) && !(birth.isEmpty())){
            item.setPatientFirstName(firstName);
            item.setPatientLastName(lastName);
            item.setPatientBirthDate(birth);

            db.Save(item);
            db.close();
            refreshdata();
        } else {
            StartErrorDialog();
        }
    }

    private void StartErrorDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View error_view = getLayoutInflater().inflate(R.layout.error_popup, null);

        errorButton = (Button) error_view.findViewById(R.id.revenir);

        dialogBuilder.setView(error_view);
        error_dialog = dialogBuilder.create();
        error_dialog.show();

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewContactDialog();
                error_dialog.dismiss();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void refreshdata() {
        patientModules.clear();

        db = new DataBaseHandler(getApplicationContext());
        final ArrayList<PatientModule> itemDataBase = db.getAllItems();
        db.close();

        for (int i = 0; i < itemDataBase.size(); i++){

            PatientModule patientModule = new PatientModule();

            String firstNameRecorded = itemDataBase.get(i).getPatientFistName();
            String lastNameRecorded = itemDataBase.get(i).getPatientLastName();
            String birthRecorded = itemDataBase.get(i).getPatientBirthDate();
            String descriptionRecorded = itemDataBase.get(i).getPatientDescription();
            ArrayList<String> acquisitionRecorded = itemDataBase.get(i).getPatientAllAcquisition();
            int idRecorded = itemDataBase.get(i).getId();

            patientModule.setPatientFirstName(firstNameRecorded);
            patientModule.setPatientLastName(lastNameRecorded);
            patientModule.setPatientBirthDate(birthRecorded);
            patientModule.setPatientDescription(descriptionRecorded);
            patientModule.setPatientAllAcquisition(acquisitionRecorded);
            patientModule.setId(idRecorded);

            patientModules.add(patientModule);

        }
        adapter.notifyDataSetChanged();
    }
}