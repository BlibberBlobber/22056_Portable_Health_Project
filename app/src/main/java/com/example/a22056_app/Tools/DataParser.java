package com.example.a22056_app.Tools;

import android.util.Log;

import com.example.a22056_app.R;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataParser {

    InputStream inputStream;

    public void getData(InputStream inputStream) throws IOException { //not void -> ArrayList<[]double>


        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);
            reader.getLinesRead();
            String[] nextLine;
            ArrayList<double[]> valueArray = new ArrayList<>();
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                double[] vals = new double[nextLine.length];
                for (int i = 0; i < nextLine.length; i++){
                    double dVal = Double.parseDouble(nextLine[i]);
                    vals[i] = dVal;
                }
                valueArray.add(vals);
                counter ++;

            }
            Log.d("DATAREADER", "Size of data array " + valueArray.size());
            Log.d("DATAREADER", "Sample from arraylist " + valueArray.get(10)[0]);
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }




    }
}
