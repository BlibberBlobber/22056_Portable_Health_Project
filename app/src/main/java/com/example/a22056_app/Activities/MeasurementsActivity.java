package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a22056_app.Models.DataPoint;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MeasurementsActivity extends AppCompatActivity {

    Handler mHandler;
    int interval = 10000;
    int intervalCounter;
    String patientName;
    Patient patient;
    ArrayList<double[]> patientFeatures;
    ArrayList<DataPoint> hrArrayList;
    ArrayList<DataPoint> hrvArrayList;
    ArrayList<DataPoint> tempArrayList;
  //  private LineGraphSeries<DataPoint> signal = new LineGraphSeries<DataPoint>();
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
        patientFeatures = (ArrayList<double[]>) extras.get("features");
        hrArrayList = (ArrayList<DataPoint>) extras.getSerializable("hr");
        hrvArrayList = (ArrayList<DataPoint>) extras.getSerializable("hrv");
        tempArrayList = (ArrayList<DataPoint>) extras.getSerializable("temp");
        intervalCounter = extras.getInt("intervalcounter");
        nameTextView.setText("Patient name: " + patientName);
        mHandler = new Handler();
        startRepeatingTask();
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
        DataPoint hrDataPoint = hrArrayList.get(intervalCounter);
        DataPoint hrvDataPoint = hrvArrayList.get(intervalCounter);
        DataPoint tempDataPoint = tempArrayList.get(intervalCounter);
        hrTextView.setText("Heart rate: " + Math.round(hrDataPoint.getValue()));
        stressTextView.setText("HRV: " + Math.round(hrvDataPoint.getValue()));
        tempTextView.setText("Temperature: " + Math.round(tempDataPoint.getValue()));

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