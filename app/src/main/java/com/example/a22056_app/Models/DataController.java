package com.example.a22056_app.Models;

import java.util.ArrayList;

public class DataController {

    String dataType;
    ArrayList<DataPoint> dataSeries = new ArrayList<>();
    //Tilføj features


    public DataController(String dataType, ArrayList<DataPoint> dataSeries){
        this.dataType = dataType;
        this.dataSeries = dataSeries;
    }

    public void calculateFeatures(){}

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public ArrayList<DataPoint> getDataSeries() {
        return dataSeries;
    }

    public void setDataSeries(ArrayList<DataPoint> dataSeries) {
        this.dataSeries = dataSeries;
    }

}
