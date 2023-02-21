package com.example.bodysway;

public class PatientModule {
    private String patientFirstName;
    private String patientLastName;
    private String patientBirthDate;
    private int id;

    public PatientModule(String patientFisrtName, String patientLastName, String patientBirthDate) {
        this.patientFirstName = patientFisrtName;
        this.patientLastName = patientLastName;
        this.patientBirthDate = patientBirthDate;
    }

    public PatientModule() {
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
}
