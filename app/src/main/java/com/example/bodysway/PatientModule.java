package com.example.bodysway;

import android.content.Context;

import java.util.ArrayList;

public class PatientModule {
    private String patientFirstName;
    private String patientLastName;
    private String patientBirthDate;
    private int id;
    private String patientDescription;

    private ArrayList<String> patientAllAcquisition;

    public PatientModule() {
    }

    public ArrayList<String> getPatientAllAcquisition() {
        if (patientAllAcquisition == null) {
            patientAllAcquisition = new ArrayList<>();
        }
        return patientAllAcquisition;
    }

    public void setPatientAllAcquisition(ArrayList<String> patientAllAcquisition) {
        this.patientAllAcquisition = patientAllAcquisition;
    }

    public String getPatientFistName() {
        return patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }
    public String getPatientBirthDate() {
        return patientBirthDate;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public void setPatientBirthDate(String patientBirthDate) {
        this.patientBirthDate = patientBirthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientDescription() {
        return patientDescription;
    }

    public void setPatientDescription(String patientDescription) {
        this.patientDescription = patientDescription;
    }

    public PatientModule getPatientFromID(int id, Context context){
        DataBaseHandler db = new DataBaseHandler(context);
        final ArrayList<PatientModule> itemDataBase = db.getAllItems();
        db.close();

        PatientModule patientModule = new PatientModule();

        for (int i = 0; i < itemDataBase.size(); i++) {

            int idRecorded = itemDataBase.get(i).getId();

            if (idRecorded == id) {
                String firstNameRecorded = itemDataBase.get(i).getPatientFistName();
                String lastNameRecorded = itemDataBase.get(i).getPatientLastName();
                String birthRecorded = itemDataBase.get(i).getPatientBirthDate();
                String descriptionRecorded = itemDataBase.get(i).getPatientDescription();

                patientModule.setPatientFirstName(firstNameRecorded);
                patientModule.setPatientLastName(lastNameRecorded);
                patientModule.setPatientBirthDate(birthRecorded);
                patientModule.setPatientDescription(descriptionRecorded);
                patientModule.setId(id);
            }
        }

        return patientModule;
    }
}
