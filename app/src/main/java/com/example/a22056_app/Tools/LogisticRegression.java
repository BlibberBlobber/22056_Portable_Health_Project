package com.example.a22056_app.Tools;

import static java.lang.Math.round;

public class LogisticRegression {

    private double[] coefficients;

    public LogisticRegression(double[] coefficients){
        this.coefficients = coefficients;
    }

    public int predict(double[] data){

        return (int)round(1/(1 + Math.exp(-(coefficients[0] + coefficients[1]*data[0] + coefficients[2]*data[1] + coefficients[3]*data[2] + coefficients[4]*data[3] + coefficients[5]*data[4]))));

    }

    public int[] predict(double[][] data){

        int M = data[0].length;
        int N = data.length;
        int[] result = new int[N];

        for (int i = 0 ; i<N; i++) {

            result[i] = predict(new double[]{data[i][0],data[i][1],data[i][2],data[i][3],data[i][4]});

        }

        return result;

    }

}
