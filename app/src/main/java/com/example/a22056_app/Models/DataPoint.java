package com.example.a22056_app.Models;

import com.jjoe64.graphview.series.DataPointInterface;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Serializable gør at man kan sætte DataPoint objekter i intent.putExtra()

public class DataPoint implements Serializable {
    String date;
   // Date date;
   // SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSSSSS");
    double value;

    public DataPoint(String dateString, double value){
        /*try {
            this.date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        } */
        this.date = dateString;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }


}
