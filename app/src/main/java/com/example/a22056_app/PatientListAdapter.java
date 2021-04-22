package com.example.a22056_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a22056_app.Models.Patient;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import static com.google.api.ResourceProto.resource;

public class PatientListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Patient> patients = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public PatientListAdapter(Context ctx, ArrayList<Patient> patients){
        this.context = ctx;
        this.patients = patients;
        layoutInflater = LayoutInflater.from(context);
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
        TextView surgeryTextView = convertView.findViewById(R.id.surgeryTextView);
        TextView riskTextView = convertView.findViewById(R.id.riskTextView);
        TextView hrvTextView = convertView.findViewById(R.id.hrvTextView);

        nameTextView.setText(patients.get(position).getUser().getFullName());
        return convertView;
    }

}
