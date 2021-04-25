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

import com.example.a22056_app.R;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataParser {

    InputStream inputStream;

    public  ArrayList<double[]> getData(InputStream inputStream) throws IOException { //not void ->>


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

            return valueArray;

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void savePrivately(String dataToStore, Context context) {

        // Creating folder with name GeekForGeeks
        File folder = context.getExternalFilesDir("Features");

        // Creating file with name gfg.txt
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
    
    
    
}
