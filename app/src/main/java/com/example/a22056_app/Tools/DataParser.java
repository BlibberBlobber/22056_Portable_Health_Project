package com.example.a22056_app.Tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a22056_app.Models.DataPair;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//   Class for parsing .csv files of patient data (raw measurements and features) and creating arraylists with the contained information

public class DataParser {

    public ArrayList<double[]> getData(InputStream inputStream) throws IOException {

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);
            String[] nextLine;
            ArrayList<double[]> valueArray = new ArrayList<>();
            int counter = 0; // reader object has no recollection of where it is the document, this counter is declared to keep track of the line number.
            while ((nextLine = reader.readNext()) != null) {
                if (counter != 0) { // features .csv files have titles for each feature on the first line. Not to be included in arraylist with feature values.
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
            return  valueArray;

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePrivately(String dataToStore, Context context) {

        File folder = context.getExternalFilesDir("Features");
        File file = new File(folder, "Feature_person1");
        Log.d("DATAREADER", "Trying to write to Documents");
        writeTextData(file, dataToStore);

    }

    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            //Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DATAREADER", "Wrote to Documents");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("DATAREADER", "Couldnt write to Documents");
                }
            }
        }
    }
    

    public ArrayList<DataPair> getMeasurements(InputStream inputStream) throws  IOException{ // method for .csv files of patient measurements
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);
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
            return  valueArray;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
