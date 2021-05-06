package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;

public class MeasurementsActivity extends AppCompatActivity {

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
    ArrayList<DataPair> hrArrayList;
    ArrayList<DataPair> hrvArrayList;
    ArrayList<DataPair> tempArrayList;
    TextView nameTextView;
    TextView stressTextView;
    TextView hrTextView;
    TextView tempTextView;
    GraphView hrGraphView;
    GraphView hrvGraphView;
    GraphView tempGraphView;

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
        Bundle extras = getIntent().getExtras();
        patientName = extras.get("name").toString();
        //patientFeatures = (ArrayList<double[]>) extras.get("features");
        pid = extras.get("pid").toString();
        //intervalCounter = extras.getInt("intervalcounter");
        nameTextView.setText("Patient name: " + patientName);
        getPatientData();
        mHandler = new Handler();
        hrGraphView.setTitle("HR signal");
        hrvGraphView.setTitle("HRV signal");
        tempGraphView.setTitle("Temperature signal");
        hrGraphView.getViewport().setScalable(true);
        hrGraphView.getViewport().setScrollable(true);
        hrvGraphView.getViewport().setScalable(true);
        hrvGraphView.getViewport().setScrollable(true);
        tempGraphView.getViewport().setScalable(true);
        tempGraphView.getViewport().setScrollable(true);
        hrGraphView.getViewport().setMinX(0);
        hrvGraphView.getViewport().setMinX(0);
        tempGraphView.getViewport().setMinX(0);
        startRepeatingTask();

    }

    public void getPatientData(){
        InputStream hrIS;
        InputStream hrvIS;
        InputStream tempIS;
        InputStream featuresIS;
        DataParser parser = new DataParser();
        if (pid.equals("1")){
            hrIS = getResources().openRawResource(R.raw.hr_person_1);
            hrvIS = getResources().openRawResource(R.raw.hrv_person_1);
            tempIS = getResources().openRawResource(R.raw.temp_person_1);
            featuresIS = getResources().openRawResource(R.raw.featuresperson1);
        } else{
            hrIS = getResources().openRawResource(R.raw.hr_person_2);
            hrvIS = getResources().openRawResource(R.raw.hrv_person_2);
            tempIS = getResources().openRawResource(R.raw.temp_person_2);
            featuresIS = getResources().openRawResource(R.raw.featuresperson2);
        }
        try {
            hrArrayList = parser.getMeasurements(hrIS);
            hrvArrayList = parser.getMeasurements(hrvIS);
            tempArrayList = parser.getMeasurements(tempIS);
            patientFeatures = parser.getData(featuresIS);
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

        }
        //hrSignal.appendData(new DataPoint(seconds, hrArrayList.get(intervalCounter).getValue()), false, 100000, true);
        //hrvSignal.appendData(new DataPoint(seconds, hrvArrayList.get(intervalCounter).getValue()), false, 100000, true);
        //tempSignal.appendData(new DataPoint(seconds, tempArrayList.get(intervalCounter).getValue()), false, 100000, true);
        Log.d("DH_TEST","Series count " + hrvGraphView.getSeries().size());
        hrGraphView.addSeries(hrSignal);
        hrvGraphView.addSeries(hrvSignal);
        tempGraphView.addSeries(tempSignal);
        hrGraphView.getViewport().setMaxX((intervalCounter*8+8)*hrTS);
        hrvGraphView.getViewport().setMaxX((intervalCounter*8+8)*hrTS);
        tempGraphView.getViewport().setMaxX((intervalCounter*4+4)*tempTS);
        hrGraphView.refreshDrawableState();
        hrvGraphView.refreshDrawableState();
        tempGraphView.refreshDrawableState();

        intervalCounter ++;
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