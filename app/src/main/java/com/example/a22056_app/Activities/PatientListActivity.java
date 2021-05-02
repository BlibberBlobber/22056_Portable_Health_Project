package com.example.a22056_app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.a22056_app.Models.DataPoint;
import com.example.a22056_app.Models.Patient;
import com.example.a22056_app.PatientListAdapter;
import com.example.a22056_app.R;
import com.example.a22056_app.Tools.DataParser;
import com.example.a22056_app.Tools.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Timer;



public class PatientListActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate {

    private EmpaDeviceManager deviceManager;
    private ListView listView;
    private Button addPatientButton;
    private PatientListAdapter listAdapter;
    private ArrayList<double[]> firstPersonFeatures;
    private ArrayList<double[]> secondPersonFeatures;
    private ArrayList<DataPoint> hrFirstPerson;
    private ArrayList<DataPoint> hrSecondPerson;
    private ArrayList<DataPoint> hrvFirstPerson;
    private ArrayList<DataPoint> hrvSecondPerson;
    private ArrayList<DataPoint> tempFirstPerson;
    private ArrayList<DataPoint> tempSecondPerson;
    private ArrayList<Patient> patients = new ArrayList<>();
   // Toolbar toolbar;
    ProgressBar progressBar;
    private int mInterval = 10000; // 10 sekunder
    private Handler mHandler;
    private int intervalCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);
        deviceManager.authenticateWithAPIKey("81eb4118b7654dd68f7d47b280e7da2b");

        DataParser parser = new DataParser();
        InputStream firstInputStream = getResources().openRawResource(R.raw.featuresperson1);
        InputStream secondInputStream = getResources().openRawResource(R.raw.featuresperson2);
        InputStream hrFirstPersonIS = getResources().openRawResource(R.raw.hr_person_1);
        InputStream hrvFirstPersonIS = getResources().openRawResource(R.raw.hrv_person_1);
        InputStream hrSecondPersonIS = getResources().openRawResource(R.raw.hr_person_2);
        InputStream hrvSecondPersonIS = getResources().openRawResource(R.raw.hrv_person_2);
        InputStream tempFirstPersonIS = getResources().openRawResource(R.raw.temp_person_1);
        InputStream tempSecondPersonIS = getResources().openRawResource(R.raw.temp_person_1);
        try {
            firstPersonFeatures = parser.getData(firstInputStream);
            secondPersonFeatures = parser.getData(secondInputStream);
            hrFirstPerson = parser.getMeasurements(hrFirstPersonIS);
            hrSecondPerson = parser.getMeasurements(hrSecondPersonIS);
            hrvFirstPerson = parser.getMeasurements(hrvFirstPersonIS);
            hrvSecondPerson = parser.getMeasurements(hrvSecondPersonIS);
            tempFirstPerson = parser.getMeasurements(tempFirstPersonIS);
            tempSecondPerson = parser.getMeasurements(tempSecondPersonIS);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DHREADER","Error appeared while calling getMeasurements()");
        }

        mHandler = new Handler();

       // toolbar = findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

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
                   // ArrayList<Patient> patients = new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Firestore",document.getId() + " => " + document.getData());
                        Patient patient = new Patient(document.getData());
                        patients.add(patient);
                    }
                    setListAdapter(patients);
                    startRepeatingTask();
                    setOnItemClick();
                }
            }
        });
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try{

            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
                updateTable();
                intervalCounter ++;
            }
        }
    };
    private void updateTable(){
        //listAdapter.setFirstPersonFeatures(firstPersonFeatures.get(intervalCounter));
        //listAdapter.setSecondPersonFeatures(secondPersonFeatures.get(intervalCounter));
        listAdapter.setHrFirstPerson(hrFirstPerson.get(intervalCounter));
        listAdapter.setHrSecondPerson(hrSecondPerson.get(intervalCounter));
        listAdapter.setTempFirstPerson(tempFirstPerson.get(intervalCounter));
        listAdapter.setTempSecondPerson(tempSecondPerson.get(intervalCounter));
        listAdapter.setHrvFirstPerson(hrvFirstPerson.get(intervalCounter));
        listAdapter.setHrSecondPerson(hrvSecondPerson.get(intervalCounter));
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
    private void setOnItemClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(PatientListActivity.this, MeasurementsActivity.class);
                Patient patient = patients.get(position);
                intent.putExtra("name", patient.getUser().getFullName());
                if (position == 0){
                    intent.putExtra("features", firstPersonFeatures);
                    intent.putExtra("hrv", hrvFirstPerson);
                    intent.putExtra("hr", hrFirstPerson);
                    intent.putExtra("temp", tempFirstPerson);
                } else{
                    intent.putExtra("features", secondPersonFeatures);
                    intent.putExtra("hrv", hrvSecondPerson);
                    intent.putExtra("hr", hrSecondPerson);
                    intent.putExtra("temp", tempSecondPerson);
                }
                intent.putExtra("intervalcounter", intervalCounter);
                startActivity(intent);
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void setListAdapter(ArrayList<Patient> patients){

        listAdapter = new PatientListAdapter(this, patients, hrFirstPerson.get(0), hrSecondPerson.get(0), tempFirstPerson.get(0), tempSecondPerson.get(0), hrvFirstPerson.get(0), hrvSecondPerson.get(0));
        listView.setAdapter(listAdapter);

    }

    private void startRepeatingTask(){
        mStatusChecker.run();
    }
    private void stopRepeatingTask(){
        mHandler.removeCallbacks(mStatusChecker);
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

        Log.d("Empatica", "Did establish connection");

    }

    @Override
    public void didUpdateSensorStatus(int status, EmpaSensorType type) {

    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice device, String deviceLabel, int rssi, boolean allowed) {
        Log.d("Empatica", "Did discover device");

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