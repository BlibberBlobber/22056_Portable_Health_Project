package com.example.a22056_app;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a22056_app.Models.DataPoint;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.Tools.Notification;

import java.util.ArrayList;

public class PatientListAdapter extends BaseAdapter {
    Notification notification = new Notification();
    private Context context;
    private ArrayList<Patient> patients = new ArrayList<>();
    private LayoutInflater layoutInflater;
    double[] firstPersonFeatures;
    double[] secondPersonFeatures;
    DataPoint tempFirstPerson;
    DataPoint tempSecondPerson;
    DataPoint hrFirstPerson;
    DataPoint hrSecondPerson;
    DataPoint hrvFirstPerson;
    DataPoint hrvSecondPerson;

    public void setTempFirstPerson(DataPoint tempFirstPerson) {
        this.tempFirstPerson = tempFirstPerson;
    }

    public void setTempSecondPerson(DataPoint tempSecondPerson) {
        this.tempSecondPerson = tempSecondPerson;
    }

    public void setHrFirstPerson(DataPoint hrFirstPerson) {
        this.hrFirstPerson = hrFirstPerson;
    }

    public void setHrSecondPerson(DataPoint hrSecondPerson) {
        this.hrSecondPerson = hrSecondPerson;
    }

    public void setHrvFirstPerson(DataPoint hrvFirstPerson) {
        this.hrvFirstPerson = hrvFirstPerson;
    }

    public void setHrvSecondPerson(DataPoint hrvSecondPerson) {
        this.hrvSecondPerson = hrvSecondPerson;
    }

    public void setFirstPersonFeatures(double[] firstPersonFeatures) {
        this.firstPersonFeatures = firstPersonFeatures;
    }

    public void setSecondPersonFeatures(double[] secondPersonFeatures) {
        this.secondPersonFeatures = secondPersonFeatures;
    }



    public PatientListAdapter(Context ctx, ArrayList<Patient> patients, double[] firstPersonFeatures, double[] secondPersonFeatures){
        this.context = ctx;
        this.patients = patients;
        layoutInflater = LayoutInflater.from(context);
        this.firstPersonFeatures = firstPersonFeatures;
        this.secondPersonFeatures = secondPersonFeatures;
    }
    public PatientListAdapter(Context ctx, ArrayList<Patient> patients, DataPoint hrFirstPerson, DataPoint hrSecondPerson, DataPoint tempFirstPerson, DataPoint tempSecondPerson, DataPoint hrvFirstPerson, DataPoint hrvSecondPerson){
        this.context = ctx;
        this.patients = patients;
        layoutInflater = LayoutInflater.from(context);
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
    }

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
            convertView = layoutInflater.inflate(R.layout.patient_list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.nameTextview);
        TextView tempTextView = convertView.findViewById(R.id.temperatureTextView);
        TextView hrTextView = convertView.findViewById(R.id.hrTextView);
        TextView stressTextView = convertView.findViewById(R.id.stressTextView);
        //String temp = "";
        //String hr = "";
        //String hrv = "";
        DataPoint hr;
        DataPoint temp;
        DataPoint hrv;

        if (position == 0){
            hr = hrFirstPerson;
            temp = tempFirstPerson;
            hrv = hrvFirstPerson;
        } else{
            hr = hrSecondPerson;
            temp = tempSecondPerson;
            hrv = hrvSecondPerson;
        }
       // hr = String.valueOf((int) Math.round(currentPerson[25]));
       // temp = String.valueOf((int) Math.round(currentPerson[34]));
        tempTextView.setText("Temperature: " + Math.round(temp.getValue()));
        hrTextView.setText("Heart rate: " + Math.round(hr.getValue()));
        stressTextView.setText("HRV: " + Math.round(hrv.getValue()));
       /* if (currentPerson[39] == 0.0){
            stressTextView.setText("Not stressed");
            stressTextView.setTextColor(Color.GREEN);
        } else {
            stressTextView.setText("Stressed");
            stressTextView.setTextColor(Color.RED);

            doNotification(nameTextView.getText().toString());
        }

        } */



        nameTextView.setText(patients.get(position).getUser().getFullName());
        return convertView;
    }

    private void doNotification(String nameText) {
        notification.addNotification(context, nameText);
    }

}
