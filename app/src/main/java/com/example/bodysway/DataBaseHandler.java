package com.example.bodysway;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bodysway.Constants;
import com.example.bodysway.PatientModule;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    private final ArrayList<PatientModule> db_list = new ArrayList<>();
    private final ArrayList<PatientModule> patient_list = new ArrayList<>();

    public DataBaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {

        String TABLEPATIENT = "CREATE TABLE " +
                Constants.TABLE_NAME + " (" +
                Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants.PATIENT_FIRSTNAME + " STRING," +
                Constants.PATIENT_LASTNAME + " STRING," +
                Constants.PATIENT_BIRTH + " STRING," +
                Constants.PATIENT_DESCRIPTION + " STRING) ;";

        db.execSQL(TABLEPATIENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(db);
    }

    public void Save(PatientModule item){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.PATIENT_FIRSTNAME, item.getPatientFistName());
        values.put(Constants.PATIENT_LASTNAME, item.getPatientLastName());
        values.put(Constants.PATIENT_BIRTH, item.getPatientBirthDate());
        values.put(Constants.PATIENT_DESCRIPTION, item.getPatientDescription());

        db.insert(Constants.TABLE_NAME, null, values);
    }

    @SuppressLint("Range")
    public ArrayList<PatientModule> getAllItems(){
        db_list.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID, Constants.PATIENT_FIRSTNAME,Constants.PATIENT_LASTNAME,Constants.PATIENT_BIRTH, Constants.PATIENT_DESCRIPTION},null,null, null, null,null);

        if (cursor.moveToFirst()){
            do {
                PatientModule item = new PatientModule();
                item.setPatientFirstName(String.valueOf(cursor.getString(1)));
                item.setPatientLastName(String.valueOf(cursor.getString(2)));
                item.setPatientBirthDate(String.valueOf(cursor.getString(3)));
                item.setPatientDescription(String.valueOf(cursor.getString(4)));
                item.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));

                db_list.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return db_list;
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=?", new String[] {String.valueOf(id)});
    }


    public void updateDB(PatientModule patientModule){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.PATIENT_FIRSTNAME, patientModule.getPatientFistName());
        values.put(Constants.PATIENT_LASTNAME, patientModule.getPatientLastName());
        values.put(Constants.PATIENT_BIRTH, patientModule.getPatientBirthDate());
        values.put(Constants.PATIENT_DESCRIPTION, patientModule.getPatientDescription());

        db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + " = " + patientModule.getId(), null);
    }
}
