package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.graphics.Color;

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
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
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
    double hrTS = 0.125;
    double tempTS = 0.25;
    int interval = 1000;
    int featuresCounter = 0;
    Date date = new Date();
    int intervalCounter = 0;
    String patientName;
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSSS");
    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
    ArrayList<double[]> patientFeatures;
    String pid;
    LineGraphSeries<DataPoint> hrSignal = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> hrvSignal = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> tempSignal = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> edaSignal = new LineGraphSeries<>();
    ArrayList<DataPair> hrArrayList;
    ArrayList<DataPair> hrvArrayList;
    ArrayList<DataPair> tempArrayList;
    ArrayList<DataPair> edaArrayList;
    TextView nameTextView;
    TextView stressTextView;
    TextView hrTextView;
    TextView tempTextView;
    GraphView hrGraphView;
    GraphView hrvGraphView;
    GraphView tempGraphView;
    GraphView edaGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        nameTextView = findViewById(R.id.nameTextView);
        stressTextView = findViewById(R.id.stressTextView);
        hrTextView = findViewById(R.id.hrTextView);
        tempTextView = findViewById(R.id.tempTextView);
        hrGraphView = findViewById(R.id.hrGraphView);
        hrvGraphView = findViewById(R.id.hrvGraphView);
        tempGraphView = findViewById(R.id.tempGraphView);
        edaGraphView = findViewById(R.id.edaGraphView);
        Bundle extras = getIntent().getExtras();
        patientName = extras.get("name").toString();
        //patientFeatures = (ArrayList<double[]>) extras.get("features");
        pid = extras.get("pid").toString();
        //intervalCounter = extras.getInt("intervalcounter");
        double test = Double.parseDouble("0.250123653846154");
        Log.d("DH","Double test " + test);

        nameTextView.setText("Patient name: " + patientName);
        getPatientData();
        mHandler = new Handler();


        double[] coefficients = {intercept,eda_features_max,eda_scl_features_mean,hr_features_mean,temp_features_std,temp_features_max};
        LogisticRegressionModel = new LogisticRegression(coefficients);


        hrGraphView.setTitle("HR signal");
        hrGraphView.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        hrvGraphView.setTitle("HRV signal");
        hrvGraphView.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        tempGraphView.setTitle("Temperature signal");
        tempGraphView.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        edaGraphView.setTitle("EDA signal");
        edaGraphView.getGridLabelRenderer().setHorizontalAxisTitle("Seconds");
        hrGraphView.getViewport().setScalable(true);
        hrGraphView.getViewport().setScrollable(true);
        hrvGraphView.getViewport().setScalable(true);
        hrvGraphView.getViewport().setScrollable(true);
        tempGraphView.getViewport().setScalable(true);
        tempGraphView.getViewport().setScrollable(true);
        edaGraphView.getViewport().setScrollable(true);
        edaGraphView.getViewport().setScalable(true);
        hrGraphView.getViewport().setMinX(0);
        hrvGraphView.getViewport().setMinX(0);
        tempGraphView.getViewport().setMinX(0);
        edaGraphView.getViewport().setMinX(0);
        startRepeatingTask();

    }

    public void getPatientData(){
        InputStream hrIS;
        InputStream hrvIS;
        InputStream tempIS;
        InputStream edaIS;
        InputStream featuresIS;
        DataParser parser = new DataParser();
        if (pid.equals("1")){
            hrIS = getResources().openRawResource(R.raw.hr_person_1);
            hrvIS = getResources().openRawResource(R.raw.hrv_person_1);
            tempIS = getResources().openRawResource(R.raw.temp_person_1);
            edaIS = getResources().openRawResource(R.raw.eda_scl_person_1);
            featuresIS = getResources().openRawResource(R.raw.featuresperson1);
        } else{
            hrIS = getResources().openRawResource(R.raw.hr_person_2);
            hrvIS = getResources().openRawResource(R.raw.hrv_person_2);
            tempIS = getResources().openRawResource(R.raw.temp_person_2);
            edaIS = getResources().openRawResource(R.raw.eda_scl_person_2);
            featuresIS = getResources().openRawResource(R.raw.featuresperson2);
        }
        try {
            hrArrayList = parser.getMeasurements(hrIS);
            hrvArrayList = parser.getMeasurements(hrvIS);
            tempArrayList = parser.getMeasurements(tempIS);
            edaArrayList = parser.getMeasurements(edaIS);
            patientFeatures = parser.getData(featuresIS);
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
            }
        }
    };
    private void updateValues(){
        Log.d("DH_TEST", "Intervl counter " + intervalCounter);
        Log.d("DH_TEST", "Features counter " + featuresCounter);
        if (featuresCounter == 10 || featuresCounter == 0) {
            double[] currentFeatures = patientFeatures.get(intervalCounter / 10);
            String hr = String.valueOf((int) Math.round(currentFeatures[25]));
            String temp = String.valueOf((int) Math.round(currentFeatures[34]));
            hrTextView.setText("Heart rate: " + hr);
            tempTextView.setText("Temperature: " + temp);
            if (currentFeatures[39] == 0.0) {
                stressTextView.setText("Not stressed");
                stressTextView.setTextColor(Color.GREEN);
            } else {
                stressTextView.setText("Stressed");
                stressTextView.setTextColor(Color.RED);
            }
            featuresCounter = 0;
        }
        featuresCounter ++;

        /*int seconds;
        if (intervalCounter == 0){
            this.date = new Date();
            seconds = 0;
        } else {
            //seconds = Math.round(((new Date()).getTime() - this.date.getTime())/1000);
            seconds = interval;
        } */
        /* DataPair hrDataPair = hrArrayList.get(intervalCounter);
        DataPair hrvDataPair = hrvArrayList.get(intervalCounter);
        DataPair tempDataPair = tempArrayList.get(intervalCounter);
        hrTextView.setText("Heart rate: " + Math.round(hrDataPair.getValue()));
        stressTextView.setText("HRV: " + Math.round(hrvDataPair.getValue()));
        tempTextView.setText("Temperature: " + Math.round(tempDataPair.getValue())); */
        //int seconds = Math.round(((new Date()).getTime() - this.date.getTime())/1000);
        /*try {
            String string = hrArrayList.get(intervalCounter).getDate().replace("'","");
            //hrDate = format.parse(string);
            seconds = Math.round(((new Date()).getTime() - this.date.getTime())/1000);
            Log.d("DATE", "Seconds passed " + seconds);
           // date = displayFormat.parse(displayFormat.format(date));
           // Log.d("DATE", "Date.getTime() " + hrDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        } */
        for (int i = intervalCounter*8; i < intervalCounter*8+8; i ++){
            //Log.d("DH_TEST", "For loop A counter " + i);
            double s = intervalCounter + (i - intervalCounter*8)*hrTS;
            hrSignal.appendData(new DataPoint(s, hrArrayList.get(i).getValue()), true, 100000, true);
            hrvSignal.appendData(new DataPoint(s, hrvArrayList.get(i).getValue()), true, 100000, true);

        }
        for (int i = intervalCounter*4; i < intervalCounter*4+4; i ++){
            //Log.d("DH_TEST", "For loop B counter " + i);
            double s = intervalCounter + (i - intervalCounter*4)*tempTS;
            tempSignal.appendData(new DataPoint(s, tempArrayList.get(i).getValue()), true, 100000, true);
            edaSignal.appendData(new DataPoint(s, edaArrayList.get(i).getValue()), true, 10000, true);


    }

        //hrSignal.appendData(new DataPoint(seconds, hrArrayList.get(intervalCounter).getValue()), false, 100000, true);
        //hrvSignal.appendData(new DataPoint(seconds, hrvArrayList.get(intervalCounter).getValue()), false, 100000, true);
        //tempSignal.appendData(new DataPoint(seconds, tempArrayList.get(intervalCounter).getValue()), false, 100000, true);
        Log.d("DH_TEST","Series count " + hrvGraphView.getSeries().size());
        hrGraphView.addSeries(hrSignal);
        hrvGraphView.addSeries(hrvSignal);
        tempGraphView.addSeries(tempSignal);
        edaGraphView.addSeries(edaSignal);
        hrGraphView.getViewport().setMaxX((intervalCounter*8+8)*hrTS);
        hrvGraphView.getViewport().setMaxX((intervalCounter*8+8)*hrTS);
        tempGraphView.getViewport().setMaxX((intervalCounter*4+4)*tempTS);
        edaGraphView.getViewport().setMaxX((intervalCounter*4+4)*tempTS);
        hrGraphView.refreshDrawableState();
        hrvGraphView.refreshDrawableState();
        tempGraphView.refreshDrawableState();

        handleStressedInfo();

        intervalCounter ++;



    }
    private void handleStressedInfo(){

        int sample_shift_to_get_to_stress_faster = 3700;
        List temp = featureList.subList(intervalCounter*40+sample_shift_to_get_to_stress_faster,(intervalCounter+1)*(40)+sample_shift_to_get_to_stress_faster);

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
