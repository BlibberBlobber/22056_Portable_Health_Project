package com.example.a22056_app;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;

import java.util.Date;

public class MeasurementDataSeries {

    private LineGraphSeries<DataPoint> signal = new LineGraphSeries<DataPoint>();
    private Datapoint datapoint;
    public MeasurementDataSeries(){}

    public void addDataPointToSeries(Date timestamp, double sample){
                signal.appendData(new DataPoint(timestamp,sample),false,10000,true);
    }

    public LineGraphSeries<DataPoint> getData(){
        return this.signal;
    }


}
