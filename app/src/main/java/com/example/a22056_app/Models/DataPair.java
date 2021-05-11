package com.example.a22056_app.Models;


import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//   Model class for variables for the value and date of a measured value for a patient.
//   Implements Serializable in order to be able to pass objects of the class as arguments in putExtra() in intent objects.
public class DataPair implements Serializable{
    String date;
    double value;

    public DataPair(String dateString, double value){
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
