package com.example.a22056_app.Tools;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;
import com.example.a22056_app.R;
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//  Class to get constant values from the config.properties in the raw folder. Primarily intended to be used to know if app is running for patient module or healthcare professional module

public class Configuration {


    public static String getConfigValue(Context context, String name) {

        try {

            Resources resources = context.getResources();//Errors of context null
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Exception e) {
            Log.e("", "Unable to find the config file: " + e.getMessage());


        }

        return "0";
    }





}
