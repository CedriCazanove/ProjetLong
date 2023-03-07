package com.example.bodysway;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

public class DisplayOutcome extends AppCompatActivity {

    private static final String TAG = "Accelerometer";

    private TextView textView;

    private Button btnSubmit;

    private Button downloadButton;

    private ImageView graphPythonAcc, graphPythonPos;

    private String xData, yData;
    private Acquisition acquisition;

    private ScatterChart scatterChart;
    private ScatterData scatterData;
    private ScatterDataSet scatterDataSet;
    private ArrayList scatterEntries;


    private LineChart lineChart;

    private ArrayList<Entry> dataVals = new ArrayList<Entry>();

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_outcome);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        // Partie affichage
        textView = (TextView) findViewById(R.id.txtDisplayOutcome);
        graphPythonAcc = (ImageView) findViewById(R.id.graphDataPython);
        graphPythonPos = (ImageView) findViewById(R.id.graphDataPython2);
        downloadButton = (Button) findViewById(R.id.downloadButton);

        xData = "";
        yData = "";

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("acquisition_filename")) {
            String filename = extras.get("acquisition_filename").toString();
            //textView.setText(filename);

            acquisition = new Acquisition().getAcquisitionFromFile(filename, this);
            textView.setText(acquisition.toString());
            for (int i = 0; i < acquisition.getMesures().size(); i++) {
                Log.d(TAG, "x(" + i + ") : " + acquisition.getMesures().get(i).getX());
                //dataVals.add(new Entry(acquisition.getMesures().get(i).getX(), acquisition.getMesures().get(i).getZ()));
                //scatterEntries.add(new BarEntry(acquisition.getMesures().get(i).getX(), acquisition.getMesures().get(i).getZ()));
                xData += acquisition.getMesures().get(i).getX() + ((i == acquisition.getMesures().size() - 1) ? "" : ",");
                yData += acquisition.getMesures().get(i).getZ() + ((i == acquisition.getMesures().size() - 1) ? "" : ",");
            }

            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
            }

            final Python py = Python.getInstance();
            PyObject pyObject = py.getModule("myscript");
            PyObject obj = pyObject.callAttr("main", xData, yData);//, acquisition.getRate());

            String str = obj.toString();

            byte data[] = android.util.Base64.decode(str, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            graphPythonAcc.setImageBitmap(bmp);

            PyObject obj2 = pyObject.callAttr("position", xData, yData, acquisition.getRate());

            String str2 = obj2.toString();

            byte data2[] = android.util.Base64.decode(str2, Base64.DEFAULT);
            Bitmap bmp2 = BitmapFactory.decodeByteArray(data2, 0, data2.length);
            graphPythonPos.setImageBitmap(bmp2);
        }

        // Partie bouton download the file
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile();
            }
        });

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            downloadButton.setEnabled(false);
        }
        else {
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String newfilename = acquisition.getFilename().substring(0, acquisition.getFilename().length()-4) + ".txt";
            file = new File(folder, newfilename);
        }
    }

    public void createFile() {
        try {
            String content = "Firstname,Lastname,AcquisitionDate,Rate,Time,x,z \r\n";
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(content.getBytes());
            List<Mesure> mesures = acquisition.getMesures();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            for (int i=0; i<mesures.size(); i++) {
                content = acquisition.getPrenom() + "," + acquisition.getNom() + "," + formatter.format(acquisition.getDateAcquisiton()) + "," + acquisition.getRate() + "," + acquisition.getTime() + "," + mesures.get(i).getX() + "," + mesures.get(i).getZ() + "\r\n";
                writer.write(content.getBytes());
            }

            writer.close();
            Toast.makeText(getApplicationContext(), "Le fichier a bien été téléchargé.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
