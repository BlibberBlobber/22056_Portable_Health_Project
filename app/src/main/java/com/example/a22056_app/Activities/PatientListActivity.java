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
import com.example.a22056_app.Models.DataPair;
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
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//   Activity to show patients and their current state in a listview for user's to get an overview. Navigation to MeasurementsActivity is performed when user clicks on a patient in the list
//   Also able to navigate to AddPatientsActivity when user presses addPatientButton.

public class PatientListActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate {

    private EmpaDeviceManager deviceManager; // not used
    private ListView listView; // list to show patients
    private Button addPatientButton;
    private PatientListAdapter listAdapter; // adapter to handle how data is incorporated into list
    private ArrayList<double[]> firstPersonFeatures;
    private ArrayList<double[]> secondPersonFeatures;
    private ArrayList<DataPair> hrFirstPerson;
    private ArrayList<DataPair> hrSecondPerson;
    private ArrayList<DataPair> hrvFirstPerson;
    private ArrayList<DataPair> hrvSecondPerson;
    private ArrayList<DataPair> tempFirstPerson;
    private ArrayList<DataPair> tempSecondPerson;
    private ArrayList<Patient> patients = new ArrayList<>();
   // Toolbar toolbar;
    ProgressBar progressBar;
    private int mInterval = 10000; // 10 seconds
    private Handler mHandler; // handler to perform updates
    private int intervalCounter = 0; // number of updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this); // unused
        deviceManager.authenticateWithAPIKey("81eb4118b7654dd68f7d47b280e7da2b");

        DataParser parser = new DataParser(); // new instance of DataParser to convert .csv files to ArrayLists
        InputStream firstInputStream = getResources().openRawResource(R.raw.featuresperson1);
        InputStream secondInputStream = getResources().openRawResource(R.raw.featuresperson2);
        InputStream hrFirstPersonIS = getResources().openRawResource(R.raw.hr_person_1);
        InputStream hrvFirstPersonIS = getResources().openRawResource(R.raw.hrv_person_1);
        InputStream hrSecondPersonIS = getResources().openRawResource(R.raw.hr_person_2);
        InputStream hrvSecondPersonIS = getResources().openRawResource(R.raw.hrv_person_2);
        InputStream tempFirstPersonIS = getResources().openRawResource(R.raw.temp_person_1);
        InputStream tempSecondPersonIS = getResources().openRawResource(R.raw.temp_person_2);
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
        listView = findViewById(R.id.patientListView);
        progressBar = findViewById(R.id.progressBar2);
        addPatientButton = findViewById(R.id.addButton);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientListActivity.this, AddPatientActivity.class);
                startActivity(intent); // go to add patients activity when user presses Add Patient button
            }
        });
        progressBar.setVisibility(View.VISIBLE); // show progress bar before attempting to retrieve patients from Firestore
        DatabaseHandler.getCollection("users", new OnCompleteListener<QuerySnapshot>() { // loading patients from Firebase Firestore
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!task.isSuccessful()){
                    Log.d("Firestore", "Error getting data " + task.getException().getMessage());
                }else {
                    for (QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Firestore",document.getId() + " => " + document.getData());
                        Patient patient = new Patient(document.getData());
                        patients.add(patient);
                    }
                    setListAdapter(patients); // set data for listview in listadapter
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
        listAdapter.setFirstPersonFeatures(firstPersonFeatures.get(intervalCounter));
        listAdapter.setSecondPersonFeatures(secondPersonFeatures.get(intervalCounter));
        listAdapter.setHrFirstPerson(hrFirstPerson.get(intervalCounter));
        listAdapter.setHrSecondPerson(hrSecondPerson.get(intervalCounter));
        listAdapter.setTempFirstPerson(tempFirstPerson.get(intervalCounter));
        listAdapter.setTempSecondPerson(tempSecondPerson.get(intervalCounter));
        listAdapter.setHrvFirstPerson(hrvFirstPerson.get(intervalCounter));
        listAdapter.setHrvSecondPerson(hrvSecondPerson.get(intervalCounter));
        listAdapter.notifyDataSetChanged(); // inform adapter that variable values have changed and that view needs to be updated
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
                    intent.putExtra("pid","1"); // if user presses click on first person on list pass pid = 1
                } else{
                    intent.putExtra("pid","2"); // if user presses click on second person on list pass pid = 2
                }
                intent.putExtra("intervalcounter", intervalCounter);
                startActivity(intent);
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void setListAdapter(ArrayList<Patient> patients){ // pass data to listadapter to show it on view

        listAdapter = new PatientListAdapter(this, patients, firstPersonFeatures.get(0), secondPersonFeatures.get(0), hrFirstPerson.get(0), hrSecondPerson.get(0), tempFirstPerson.get(0), tempSecondPerson.get(0), hrvFirstPerson.get(0), hrvSecondPerson.get(0));
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