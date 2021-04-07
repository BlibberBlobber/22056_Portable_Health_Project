package com.example.a22056_app.Tools;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;
import com.example.a22056_app.R;
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
