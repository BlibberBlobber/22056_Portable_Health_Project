package com.example.a22056_app.Models;


import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Serializable gør at man kan sætte DataPoint objekter i intent.putExtra()

public class DataPair implements Serializable{
    String date;
    //Date date;
    //SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSSS");
    double value;

    public DataPair(String dateString, double value){
        /*try {
            this.date = format.parse(dateString);
        } catch (ParseException e) {
            Log.d("DATAREADER","Error appeared parsing date string " + dateString);
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
