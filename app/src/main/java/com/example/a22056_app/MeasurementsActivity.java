package com.example.a22056_app;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;

import java.time.LocalDate;
import java.util.Date;

public class MeasurementsActivity extends AppCompatActivity {
    private MeasurementDataSeries measurementDataSeries;
    public GraphView graphView;
    //private LineGraphSeries<DataPoint> lineGraph = new LineGraphSeries<DataPoint>();
    //private Datapoint datapoint;

    public MeasurementsActivity(){}

    public void UpdateGraphviewWithData(){
        graphView = (GraphView) findViewById(R.id.idGraphView);
        //LineGraphSeries<DataPoint> data = measurementDataSeries.getData();

        // set colors and other options for the graph here
        // measurementDataSeries.setColor(getColor(R.color.TextColor)); From a color we save as a resource probably

        graphView.addSeries(measurementDataSeries.getData());

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
    }

    private void checkPermissions(){
    }

    // Do this activity get data from storage on the phone
    // Do we get the data from the cloud
    // Do we have a seperate handler for getting newest data from the watch and save it on the phone and then get data form there?
    // if so, then we cant scroll back and see old data or else we need short term view and long term view


    public void runexample(){

        // add some datapoints to the series

        Log.i("GraphError","runexample");

        try {
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 7, 12, 0).toDate(), 12);
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 8, 12, 0).toDate(), 10);
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 9, 12, 0).toDate(), 5);
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 10, 12, 0).toDate(), 18);
        } catch(Exception e){
            Log.i("GraphError", String.valueOf(e));
        }



        UpdateGraphviewWithData();

    }



}
