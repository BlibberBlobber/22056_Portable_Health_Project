package com.example.a22056_app.Tools;

import android.util.Log;

import com.example.a22056_app.Models.DataPair;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataParser {

    InputStream inputStream;

    public ArrayList<double[]> getData(InputStream inputStream) throws IOException { //not void -> ArrayList<[]double>


        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);
            reader.getLinesRead();
            String[] nextLine;
            ArrayList<double[]> valueArray = new ArrayList<>();
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (counter != 0) {
                    double[] vals = new double[nextLine.length];
                    for (int i = 0; i < nextLine.length; i++) {
                        if (i != 40) {
                            double dVal = Double.parseDouble(nextLine[i]);
                            vals[i] = dVal;
                        }
                    }
                    valueArray.add(vals);
                }
                counter ++;
            }
           /* Log.d("DATAREADER", "Size of data array " + valueArray.size());
            Log.d("DATAREADER", "Sample from arraylist " + valueArray.get(10)[0]);
            Log.d("DATAREADER", "Sample date from arraylist " + valueArray.get(10)[1]); */
            return  valueArray;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return null;
        }




    }

    public ArrayList<DataPair> getMeasurements(InputStream inputStream) throws  IOException{
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);
            reader.getLinesRead();
            String[] nextLine;
            ArrayList<DataPair> valueArray = new ArrayList<>();
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                String date = "";
                double value = 0;
                for (int i = 0; i < nextLine.length; i++) {
                    if (i == 0) {
                        value = Double.parseDouble(nextLine[i]);
                    } else {
                        date = nextLine[i];
                        date = date.replace("'","");
                    }
                }
                DataPair dataPair = new DataPair(date, value);
                valueArray.add(dataPair);
                counter ++;
            }
            //Log.d("DATAREADER", "Size of data array in get measurements " + valueArray.size());
            //Log.d("DATAREADER", "Sample value from arraylist in measurements" + valueArray.get(10).getValue());
            //Log.d("DATAREADER", "Sample date from arraylist in measurements" + valueArray.get(10).getDate());

            return  valueArray;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return null;
        }

    }
}
