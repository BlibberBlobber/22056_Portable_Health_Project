package com.example.a22056_app.Tools;

import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.List;


public class LogisticRegression {

    private double[] coefficients;

    public LogisticRegression(double[] coefficients){
        this.coefficients = coefficients;
    }

    public int predict(double[] data){

        return (int)round(1/(1 + Math.exp(-(coefficients[0] + coefficients[1]*data[0] + coefficients[2]*data[1] + coefficients[3]*data[2] + coefficients[4]*data[3] + coefficients[5]*data[4]))));

    }

    public int[] predict(ArrayList<double[]> data){

        int M = data.get(0).length;
        int N = data.size();
        int[] result = new int[N];

        for (int i = 0 ; i<N; i++) {

            result[i] = predict(new double[]{data.get(i)[17],data.get(i)[19],data.get(i)[25],data.get(i)[35],data.get(i)[37]});
            //Log.i("LinearRegression", String.valueOf(result[i]));
        }

        return result;

    }

    public int[] predict(List<double[]> data){

        int M = data.get(0).length;
        int N = data.size();
        int[] result = new int[N];

        for (int i = 0 ; i<N; i++) {

            result[i] = predict(new double[]{data.get(i)[17],data.get(i)[19],data.get(i)[25],data.get(i)[35],data.get(i)[37]});
            //Log.i("LinearRegression", String.valueOf(result[i]));
        }

        return result;

    }

}
