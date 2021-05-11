package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.a22056_app.R;
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//   Activity to simulate how patients could be added. The functionality to do so has not been implemented.
public class AddPatientActivity extends AppCompatActivity {
    Button addPatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        addPatientButton = findViewById(R.id.addButton);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPatientActivity.this, PatientListActivity.class);
                startActivity(intent);
            }
        });
    }
}