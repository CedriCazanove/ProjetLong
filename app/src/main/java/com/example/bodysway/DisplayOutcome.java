package com.example.bodysway;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class DisplayOutcome extends AppCompatActivity {

    private static final String TAG = "Accelerometer";

    private TextView textView;

    private Button btnSubmit;

    private ImageView imageView;

    private String xData, yData;

    private ScatterChart scatterChart;
    private ScatterData scatterData;
    private ScatterDataSet scatterDataSet;
    private ArrayList scatterEntries;


    private LineChart lineChart;

    private ArrayList<Entry> dataVals = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_outcome);

        textView = (TextView) findViewById(R.id.txtDisplayOutcome);
        imageView = (ImageView) findViewById(R.id.graphDataPython);

        xData = "";
        yData = "";

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("acquisition_filename")) {
            String filename = extras.get("acquisition_filename").toString();
            //textView.setText(filename);

            Acquisition acquisition = new Acquisition().getAcquisitionFromFile(filename, this);
            textView.setText(acquisition.toString());
            for (int i = 0; i < acquisition.getMesures().size(); i++) {
                Log.d(TAG, "x(" + i + ") : " + acquisition.getMesures().get(i).getX());
                //dataVals.add(new Entry(acquisition.getMesures().get(i).getX(), acquisition.getMesures().get(i).getZ()));
                //scatterEntries.add(new BarEntry(acquisition.getMesures().get(i).getX(), acquisition.getMesures().get(i).getZ()));
                xData += acquisition.getMesures().get(i).getX() + ((i == acquisition.getMesures().size() - 1) ? "":",");
                yData += acquisition.getMesures().get(i).getZ() + ((i == acquisition.getMesures().size() - 1) ? "":",");
            }

            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
            }

            final Python py = Python.getInstance();
            PyObject pyObject = py.getModule("myscript");
            PyObject obj = pyObject.callAttr("main", xData, yData);

            String str = obj.toString();

            byte data[] = android.util.Base64.decode(str, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setImageBitmap(bmp);
        }
    }
}
