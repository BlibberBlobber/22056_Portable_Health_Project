package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.a22056_app.MeasurementDataSeries;
import com.example.a22056_app.MeasurementsActivity;
import com.example.a22056_app.R;
import com.jjoe64.graphview.GraphView;

public class GraphsActivity extends AppCompatActivity {

    private MeasurementDataSeries measurementDataSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("GraphError","Main");

        com.example.a22056_app.MeasurementsActivity test = new MeasurementsActivity();
        MeasurementDataSeries measurementDataSeries = new MeasurementDataSeries();

        try {
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 7, 12, 0).toDate(), 12);
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 8, 12, 0).toDate(), 10);
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 9, 12, 0).toDate(), 5);
            measurementDataSeries.addDataPointToSeries(new org.joda.time.DateTime(2021, 4, 10, 12, 0).toDate(), 18);
        } catch(Exception e){
            Log.i("GraphError", String.valueOf(e));
        }

        //GraphView graphView = (GraphView) findViewById(R.id.idGraphView);
        //LineGraphSeries<DataPoint> data = measurementDataSeries.getData();

        // set colors and other options for the graph here
        // measurementDataSeries.setColor(getColor(R.color.TextColor)); From a color we save as a resource probably

      //  graphView.addSeries(measurementDataSeries.getData());

       // graphView.getViewport().setScalable(true);
       // graphView.getViewport().setScrollable(true);

    }
}