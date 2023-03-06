package com.example.bodysway.ui.acquisition;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bodysway.Acquisition;
import com.example.bodysway.DataBaseHandler;
import com.example.bodysway.Mesure;
import com.example.bodysway.PatientModule;
import com.example.bodysway.R;
import com.example.bodysway.databinding.FragmentAcquisitionBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AcquisitionFragment extends Fragment{

    private static final String TAG = "Accelerometer";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float ax, ay, az;

    private Long timeInit;

    private Boolean stateAcceleremoterSensor = true;

    private FragmentAcquisitionBinding binding;

    private FloatingActionButton btnSetAcquisition;

    private Button btnStartAcquisition, btnStopSettingAcquisition;

    private TextView textView, txtRate;

    private NumberPicker pickRate, pickTime;

    private LineChart lineChartX, lineChartZ;

    private LineData dataX, dataZ;

    private List<Mesure> mesureList = new ArrayList<Mesure>();

    private Thread thread;
    private Boolean plotData = true;

    private AlertDialog.Builder dialogBuilderSetAcquisition, dialogBuilderOutcome;

    private AlertDialog dialogStartAcquisition, dialogOutcome;

    private PatientModule patientModule;

    private DataBaseHandler db;

    private int idPatient;

    private RadioGroup radioGroup;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AcquitisionViewModel acquisitionViewModel = new ViewModelProvider(this).get(AcquitisionViewModel.class);

        binding = FragmentAcquisitionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle extra = getActivity().getIntent().getExtras();
        idPatient = extra.getInt("ID");
        patientModule = new PatientModule().getPatientFromID(idPatient, getContext());

        txtRate = (TextView) binding.txtRate;

        lineChartX = binding.graphX;
        lineChartX.getDescription().setText("");
        lineChartX.getDescription().setTextColor(Color.RED);
        lineChartX.getDescription().setTextSize(10);

        lineChartX.setTouchEnabled(true);
        lineChartX.setDragEnabled(true);
        lineChartX.setScaleEnabled(true);
        lineChartX.setPinchZoom(true);

        dataX = new LineData();
        dataX.setValueTextColor(Color.WHITE);
        lineChartX.setData(dataX);

        lineChartZ = binding.graphZ;
        lineChartZ.getDescription().setText("");
        lineChartZ.getDescription().setTextColor(Color.RED);
        lineChartZ.getDescription().setTextSize(10);

        lineChartZ.setTouchEnabled(true);
        lineChartZ.setDragEnabled(true);
        lineChartZ.setScaleEnabled(true);
        lineChartZ.setPinchZoom(true);

        dataZ = new LineData();
        dataZ.setValueTextColor(Color.WHITE);
        lineChartZ.setData(dataZ);

        startPlot();

        textView = binding.textAcquisition;
        textView.setText("Acquisition");

        btnSetAcquisition = binding.btnSetAcquisition;

        //initier les sensors accelerometer
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        startSensor(stateAcceleremoterSensor, accelerometer.getMaxDelay());
        updateColorBtnJobControl(stateAcceleremoterSensor);
        txtRate.setText("Fréquence d'acquisition : " + (int) (1 / (1e-6 * accelerometer.getMaxDelay())) + " Hz");
        btnSetAcquisition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAcceleremoterSensor = !stateAcceleremoterSensor;
                if (stateAcceleremoterSensor) {
                    setAcquisition();
                } else {
                    startSensor(stateAcceleremoterSensor, 0);
                    updateColorBtnJobControl(stateAcceleremoterSensor);
                }
            }
        });
        return root;
    }

    private void startPlot() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void addEntry(SensorEvent event) {
        dataX = lineChartX.getData();

        Long currentTime = System.currentTimeMillis()/1000;
        Long timeSpendInt = (currentTime - timeInit);
        String timeSpend = timeSpendInt.toString();
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];
        textView.setText("Durée de l'acquisition: " + timeSpend);

        Mesure mesure = new Mesure();
        if (dataX != null) {

            ILineDataSet setX = dataX.getDataSetByIndex(0);

            if (setX == null) {
                setX = createSet("Axe transversal (m/s²)");
                dataX.addDataSet(setX);
            }

            dataX.addEntry(new Entry(setX.getEntryCount(), ax), 0);
            mesure.setX(ax);
            dataX.notifyDataChanged();

            lineChartX.notifyDataSetChanged();
            lineChartX.setMaxVisibleValueCount(150);
            lineChartX.moveViewToX(dataX.getEntryCount());
        }

        dataZ = lineChartZ.getData();

        if (dataZ != null) {

            ILineDataSet setZ = dataZ.getDataSetByIndex(0);

            if (setZ == null) {
                setZ = createSet("Axe antéro-postérieur (m/s²)");
                dataZ.addDataSet(setZ);
            }

            dataZ.addEntry(new Entry(setZ.getEntryCount(), az), 0);
            mesure.setZ(az);
            dataZ.notifyDataChanged();

            lineChartZ.notifyDataSetChanged();
            lineChartZ.setMaxVisibleValueCount(150);
            lineChartZ.moveViewToX(dataZ.getEntryCount());
        }
        mesureList.add(mesure);
    }

    private LineDataSet createSet(String name) {
        LineDataSet set = new LineDataSet(null, name);
        set.setDrawCircles(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (plotData) {
                addEntry(event);
                plotData = false;
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onResume() {
        stateAcceleremoterSensor = false;
        updateColorBtnJobControl(stateAcceleremoterSensor);
        txtRate.setText("Fréquence d'acquisition : " + (int) (1 / (1e-6 * accelerometer.getMaxDelay())) + " Hz");
        sensorManager.registerListener(listener, accelerometer, accelerometer.getMaxDelay());
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        lineChartX.getData().removeDataSet(0);
        lineChartZ.getData().removeDataSet(0);
        if (thread != null) {
            thread.interrupt();
        }
        sensorManager.unregisterListener(listener, accelerometer);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lineChartX.getData().removeDataSet(0);
        lineChartZ.getData().removeDataSet(0);
        binding = null;
    }

    /**
     * Methode pour mettre à jour l'état du btn de Control
     * @param state
     */
    private void updateColorBtnJobControl(Boolean state) {
        if (state) {
            btnSetAcquisition.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            btnSetAcquisition.setImageResource(R.drawable.arretez);
        } else {
            btnSetAcquisition.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            btnSetAcquisition.setImageResource(R.drawable.bouton_jouer);
        }
    }

    /**
     * start or stop the accelerometer sensor
     * @param state
     * @param period in us
     */
    private void startSensor(Boolean state, int period) {
        if (state) {
            timeInit = System.currentTimeMillis()/1000;
            sensorManager.registerListener(listener, accelerometer, period);
        } else {
            //arreter le sensor lorsque le job est arreté
            sensorManager.unregisterListener(listener, accelerometer);
        }
    }

    public void setAcquisition(){
        dialogBuilderSetAcquisition = new AlertDialog.Builder(getContext());
        final View setAnAcquisitionPopUpView = getLayoutInflater().inflate(R.layout.set_an_acquisition, null);
        pickRate = setAnAcquisitionPopUpView.findViewById(R.id.pickTheRate);
        pickRate.setMinValue((int) (1 / (1e-6 * accelerometer.getMaxDelay())));
        pickRate.setMaxValue((int) (1 / (1e-6 * accelerometer.getMinDelay())));

        pickTime = setAnAcquisitionPopUpView.findViewById(R.id.pickTheTime);
        pickTime.setMinValue(1);
        pickTime.setMaxValue(60);

        radioGroup = setAnAcquisitionPopUpView.findViewById(R.id.rgEyes);
        radioGroup.check(R.id.EO);

        btnStartAcquisition = (Button) setAnAcquisitionPopUpView.findViewById(R.id.btnStartAcquisiton);
        btnStartAcquisition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean eyesOpen = (radioGroup.getCheckedRadioButtonId() == R.id.EO);
                //Toast.makeText(getContext(), "eyes " + (eyesOpen ? "open" : "closed"), Toast.LENGTH_SHORT).show();
                int time = 0;//s
                int rate = 0;//Hz
                time = pickTime.getValue();
                rate = pickRate.getValue();
                if (time > 0 && rate > 0) {
                    startAcquisition(time, (int) ((1.0 / rate) * 1000000), eyesOpen);
                    updateColorBtnJobControl(stateAcceleremoterSensor);
                    dialogStartAcquisition.dismiss();
                }
            }
        });

        btnStopSettingAcquisition = (Button) setAnAcquisitionPopUpView.findViewById(R.id.btnCancelSettingAcquisition);
        btnStopSettingAcquisition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAcceleremoterSensor = true;
                dialogStartAcquisition.dismiss();
            }
        });

        dialogBuilderSetAcquisition.setView(setAnAcquisitionPopUpView);
        dialogStartAcquisition = dialogBuilderSetAcquisition.create();
        dialogStartAcquisition.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogStartAcquisition.show();
    }

    /**
     * start or stop the accelerometer sensor
     * @param time in s
     * @param periode in us
     */
    private void startAcquisition(int time, int periode, boolean eyesOpen) {
        mesureList.clear();
        lineChartX.getData().removeDataSet(0);
        lineChartZ.getData().removeDataSet(0);


        txtRate.setText("Fréquence d'acquisition : " + (int)((1.0 / (1e-6 * periode))) + " Hz");

        Thread threadChrono = new Thread(new Runnable() {
            @Override
            public void run() {
                startSensor(stateAcceleremoterSensor, periode);
                updateColorBtnJobControl(stateAcceleremoterSensor);
                try {
                    Thread.sleep(time * 1000);
                    stateAcceleremoterSensor = !stateAcceleremoterSensor;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSensor(stateAcceleremoterSensor, 0);
                updateColorBtnJobControl(stateAcceleremoterSensor);
                saveData(time, (int)((1.0 / (1e-6 * periode))), eyesOpen);
            }
        });
        threadChrono.start();
    }

    private void saveData(int time, int rate, boolean eyesOpen) {


        LineData dataToSaveX = lineChartX.getData();
        LineData dataToSaveZ = lineChartZ.getData();
        if (dataToSaveX.getEntryCount() == dataToSaveZ.getEntryCount()) {
            Acquisition acquisition = new Acquisition(patientModule.getPatientLastName(), patientModule.getPatientFistName(), patientModule.getId(), rate, time);
            Log.d(TAG, "Acquisition :\n" + acquisition.toString());
            acquisition.setMesures(mesureList);
            acquisition.setEyesOpen(eyesOpen);
            String filename = "acquisition_" + acquisition.getDateString().replace(" ", "").replace(":", "").replace("/","") + "_" + acquisition.getNom() + "_" + acquisition.getPrenom() + "_" + acquisition.getIdPatient() + ".xml";

            ArrayList<String> acquisitionPatient = patientModule.getPatientAllAcquisition();
            acquisitionPatient.add(filename);
            patientModule.setPatientAllAcquisition(acquisitionPatient);
            db = new DataBaseHandler(getContext());
            db.updateDB(patientModule);
            db.close();

            //patientModule = new PatientModule().getPatientFromID(idPatient, getContext());
            //Log.d(TAG, "patient file : " + patientModule.getPatientAllAcquisition().get(0).toString());

            acquisition.setFilename(filename);
            File dir = getContext().getFilesDir();
            File fileData = new File(dir, filename);
            acquisition.saveAcquisition(filename, getContext());
            if ((fileData.exists())) {
                String dataSaved = readRawData(filename);
                Acquisition acquisition1 = new Acquisition().getAcquisitionFromFile(filename, getContext());
                Log.d(TAG, "Read raw data from file :" + acquisition1.getDateAcquisiton().toString());
            }
        } else {
            Log.d(TAG, "Something went wrong for the acquisition");
        }
    }

    public String readRawData(String filename) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        Log.d(TAG, "readRawData");
        String rawData = "";
        try {
            fis = getContext().openFileInput(filename);
            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            rawData = new String(inputBuffer);
            Log.d(TAG, "Read data from file :\n" + rawData);
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "readRawData end");
        return rawData;
    }
}