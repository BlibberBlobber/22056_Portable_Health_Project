package com.example.a22056_app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.PatientListAdapter;
import com.example.a22056_app.R;
import com.example.a22056_app.Tools.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class PatientListActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate {

    private EmpaDeviceManager deviceManager;
    private ListView listView;
    private Button addPatientButton;
    private PatientListAdapter listAdapter;
    Toolbar toolbar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);
        deviceManager.authenticateWithAPIKey("81eb4118b7654dd68f7d47b280e7da2b");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.patientListView);
        progressBar = findViewById(R.id.progressBar2);
        addPatientButton = findViewById(R.id.addButton);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientListActivity.this, AddPatientActivity.class);
                startActivity(intent);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        DatabaseHandler.getCollection("users", new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!task.isSuccessful()){
                    Log.d("Firestore", "Error getting data " + task.getException().getMessage());
                }else {
                    ArrayList<Patient> patients = new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Firestore",document.getId() + " => " + document.getData());
                        Patient patient = new Patient(document.getData());
                        patients.add(patient);
                    }
                    setListAdapter(patients);
                }
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void setListAdapter(ArrayList<Patient> patients){

        listAdapter = new PatientListAdapter(this, patients);
        listView.setAdapter(listAdapter);

    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {

    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {

    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {

    }

    @Override
    public void didReceiveTemperature(float t, double timestamp) {

    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {

    }

    @Override
    public void didReceiveBatteryLevel(float level, double timestamp) {

    }

    @Override
    public void didReceiveTag(double timestamp) {

    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {

    }

    @Override
    public void didEstablishConnection() {

    }

    @Override
    public void didUpdateSensorStatus(int status, EmpaSensorType type) {

    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice device, String deviceLabel, int rssi, boolean allowed) {

    }

    @Override
    public void didFailedScanning(int errorCode) {

    }

    @Override
    public void didRequestEnableBluetooth() {

    }

    @Override
    public void bluetoothStateChanged() {

    }

    @Override
    public void didUpdateOnWristStatus(int status) {

    }
}