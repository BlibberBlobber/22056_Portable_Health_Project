package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.a22056_app.Models.DataPair;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.R;
import com.example.a22056_app.Tools.DataParser;
import com.example.a22056_app.Tools.LogisticRegression;
import com.example.a22056_app.Tools.Notification;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MeasurementsActivity extends AppCompatActivity {

    Notification notification = new Notification();
    private static final double intercept = -1.57844843892432;
    private static final double eda_features_max =-1.57432446249465;
    private static final double eda_scl_features_mean = -0.615271239848967;
    private static final double hr_features_mean = -0.868413788939345;
    private static final double temp_features_std = -0.751876628298134;
    private static final double temp_features_max = 1.81182633691859;
    boolean STRESS = false;

    LogisticRegression LogisticRegressionModel;
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
    ArrayList<double[]> featureList;
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

        double[] coefficients = {intercept,eda_features_max,eda_scl_features_mean,hr_features_mean,temp_features_std,temp_features_max};
        LogisticRegressionModel = new LogisticRegression(coefficients);

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

        InputStream inputStream = getResources().openRawResource(R.raw.featuredata_tp2_s6);
        try {
            featureList = parser.getData(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
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

        handleStressedInfo();


    }

    private void handleStressedInfo(){

        List temp = featureList.subList(intervalCounter*40+4600,(intervalCounter+1)*(40)+4600);

        int[] prediction = LogisticRegressionModel.predict(temp);

        int sum = 0;
        for (Integer d: prediction) sum += d;
        if (sum >= prediction.length/2 && STRESS == false) {
            STRESS = true;
            //stressTextView.setText("Stressed");
            // send out notification

            doNotification(patientName);


        } else if (sum < prediction.length/2 && STRESS == true) {
            STRESS = false;
            //stressTextView.setText("Not stressed");
        }


    }

    private void doNotification(String nameText) {
        notification.addNotification(getApplicationContext(), nameText);
        //Log.i("DEBUG_notification", "nameText");
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