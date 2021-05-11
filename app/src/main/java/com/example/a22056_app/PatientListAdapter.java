package com.example.a22056_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a22056_app.Models.DataPair;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.Tools.Notification;

import java.util.ArrayList;
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//   Adapter to handle presentation of patient data in listview
public class PatientListAdapter extends BaseAdapter {
    Notification notification = new Notification();
    private Context context;
    private ArrayList<Patient> patients = new ArrayList<>();
    private LayoutInflater layoutInflater;
    double[] firstPersonFeatures;
    double[] secondPersonFeatures;
    DataPair tempFirstPerson;
    DataPair tempSecondPerson;
    DataPair hrFirstPerson;
    DataPair hrSecondPerson;
    DataPair hrvFirstPerson;
    DataPair hrvSecondPerson;

    public void setTempFirstPerson(DataPair tempFirstPerson) {
        this.tempFirstPerson = tempFirstPerson;
    }

    public void setTempSecondPerson(DataPair tempSecondPerson) {
        this.tempSecondPerson = tempSecondPerson;
    }

    public void setHrFirstPerson(DataPair hrFirstPerson) {
        this.hrFirstPerson = hrFirstPerson;
    }

    public void setHrSecondPerson(DataPair hrSecondPerson) {
        this.hrSecondPerson = hrSecondPerson;
    }

    public void setHrvFirstPerson(DataPair hrvFirstPerson) {
        this.hrvFirstPerson = hrvFirstPerson;
    }

    public void setHrvSecondPerson(DataPair hrvSecondPerson) {
        this.hrvSecondPerson = hrvSecondPerson;
    }

    public void setFirstPersonFeatures(double[] firstPersonFeatures) {
        this.firstPersonFeatures = firstPersonFeatures;
    }

    public void setSecondPersonFeatures(double[] secondPersonFeatures) {
        this.secondPersonFeatures = secondPersonFeatures;
    }

    public PatientListAdapter(Context ctx, ArrayList<Patient> patients, double[] firstPersonFeatures, double[] secondPersonFeatures, DataPair hrFirstPerson, DataPair hrSecondPerson, DataPair tempFirstPerson, DataPair tempSecondPerson, DataPair hrvFirstPerson, DataPair hrvSecondPerson){
        this.context = ctx;
        this.patients = patients;
        layoutInflater = LayoutInflater.from(context);
        this.firstPersonFeatures = firstPersonFeatures;
        this.hrFirstPerson = hrFirstPerson;
        this.hrSecondPerson = hrSecondPerson;
        this.tempFirstPerson = tempFirstPerson;
        this.tempSecondPerson = tempSecondPerson;
        this.hrvFirstPerson = hrvFirstPerson;
        this.hrvSecondPerson = hrvSecondPerson;
    }


    @Override
    public int getCount() {
        return patients.size();
    } // number of listview cells is determined by the number of patients

    @Override
    public Object getItem(int position) {
        return patients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.patient_list_item, parent, false); // inflates custom cell for listview
        }

        TextView nameTextView = convertView.findViewById(R.id.nameTextview);
        TextView tempTextView = convertView.findViewById(R.id.temperatureTextView);
        TextView hrTextView = convertView.findViewById(R.id.hrTextView);
        TextView stressTextView = convertView.findViewById(R.id.stressTextView);
        String temp = "";
        String hr = "";
        double[] currentPerson;

        if (position == 0){
            currentPerson = firstPersonFeatures;
        } else{
            currentPerson = secondPersonFeatures;
        }
        hr = String.valueOf((int) Math.round(currentPerson[25])); // removes decimals and converts to string
        temp = String.valueOf((int) Math.round(currentPerson[34]));
        tempTextView.setText("Temperature: " + temp);
        hrTextView.setText("Heart rate: " + hr);
        if (currentPerson[39] == 0.0){ // when this values is 0.0 the patient has been categorized as not stressed
            stressTextView.setText("Not stressed");
            stressTextView.setTextColor(Color.GREEN);
        } else {
            stressTextView.setText("Stressed");
            stressTextView.setTextColor(Color.RED);
            doNotification(nameTextView.getText().toString()); // notify user about patient being stressed
        }

        nameTextView.setText(patients.get(position).getUser().getFullName());
        return convertView;
        }

    private void doNotification(String nameText) {
        notification.addNotification(context, nameText);
    }

        }
//

