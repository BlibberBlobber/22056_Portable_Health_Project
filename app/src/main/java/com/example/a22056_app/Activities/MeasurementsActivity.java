package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.a22056_app.Models.DataPair;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.R;
import com.example.a22056_app.Tools.DataParser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeasurementsActivity extends AppCompatActivity {

    Handler mHandler;
    int interval = 10000;
    int intervalCounter;
    String patientName;
    Patient patient;
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSSS");
    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
    //ArrayList<double[]> patientFeatures;
    String pid;
    ArrayList<DataPair> hrArrayList;
    ArrayList<DataPair> hrvArrayList;
    ArrayList<DataPair> tempArrayList;
    private LineGraphSeries<DataPoint> signal = new LineGraphSeries<DataPoint>();
    TextView nameTextView;
    TextView stressTextView;
    TextView hrTextView;
    TextView tempTextView;
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        nameTextView = findViewById(R.id.nameTextView);
        stressTextView = findViewById(R.id.stressTextView);
        hrTextView = findViewById(R.id.hrTextView);
        tempTextView = findViewById(R.id.tempTextView);
        graphView = findViewById(R.id.hrGraphView);
        Bundle extras = getIntent().getExtras();
        patientName = extras.get("name").toString();
        //patientFeatures = (ArrayList<double[]>) extras.get("features");
        pid = extras.get("pid").toString();
        intervalCounter = extras.getInt("intervalcounter");
        nameTextView.setText("Patient name: " + patientName);
        getPatientData();
        mHandler = new Handler();
        startRepeatingTask();

    }

    public void getPatientData(){
        InputStream hrIS;
        InputStream hrvIS;
        InputStream tempIS;
        DataParser parser = new DataParser();
        if (pid.equals("1")){
            hrIS = getResources().openRawResource(R.raw.hr_person_1);
            hrvIS = getResources().openRawResource(R.raw.hrv_person_1);
            tempIS = getResources().openRawResource(R.raw.temp_person_1);
        } else{
            hrIS = getResources().openRawResource(R.raw.hr_person_2);
            hrvIS = getResources().openRawResource(R.raw.hrv_person_2);
            tempIS = getResources().openRawResource(R.raw.temp_person_2);
        }
        try {
            hrArrayList = parser.getMeasurements(hrIS);
            hrvArrayList = parser.getMeasurements(hrvIS);
            tempArrayList = parser.getMeasurements(tempIS);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DHREADER","Error appeared while calling getMeasurements()");
        }

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try{

            } finally {
                mHandler.postDelayed(mStatusChecker, interval);
                updateValues();
                intervalCounter ++;
            }
        }
    };
    private void updateValues(){
       /* double[] currentFeatures = patientFeatures.get(intervalCounter);
        String hr = String.valueOf((int) Math.round(currentFeatures[25]));
        String temp = String.valueOf((int) Math.round(currentFeatures[34]));
        hrTextView.setText("Heart rate: " + hr);
        tempTextView.setText("Temperature: " + temp);
        if (currentFeatures[39] == 0.0){
            stressTextView.setText("Not stressed");
            stressTextView.setTextColor(Color.GREEN);
        } else {
            stressTextView.setText("Stressed");
            stressTextView.setTextColor(Color.RED);
        }
        signal.appendData(new DataPoint(intervalCounter, currentFeatures[25]), false, 100000, true);
        graphView.addSeries(signal);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true); */
        DataPair hrDataPair = hrArrayList.get(intervalCounter);
        DataPair hrvDataPair = hrvArrayList.get(intervalCounter);
        DataPair tempDataPair = tempArrayList.get(intervalCounter);
        hrTextView.setText("Heart rate: " + Math.round(hrDataPair.getValue()));
        stressTextView.setText("HRV: " + Math.round(hrvDataPair.getValue()));
        tempTextView.setText("Temperature: " + Math.round(tempDataPair.getValue()));
        Date date = new Date();
        try {
            String string = hrArrayList.get(intervalCounter).getDate().replace("'","");
            date = format.parse(string);
            Log.d("DATE", "Date after parsing " + date);
           // date = displayFormat.parse(displayFormat.format(date));
            Log.d("DATE", "Date.getTime() " + date.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        signal.appendData(new DataPoint(date.getTime(), hrArrayList.get(intervalCounter).getValue()), false, 100000, true);
        graphView.addSeries(signal);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);

    }
    private void startRepeatingTask(){
        mStatusChecker.run();
    }
    private void stopRepeatingTask(){
        mHandler.removeCallbacks(mStatusChecker);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
}