package com.example.a22056_app.Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

        //   Developed with Java 1.8 . Please send bug reports to
        //   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
        //   Year  :  2021
        //   University  :  Technical University of Denmark
        //   ***********************************************************************

        //   Patient is a model class for storing patient information in a object. The class has two constructors:
        //   - First constructor takes an user object, a string representing the patients CPR number and a string for the patient location as argumemts.
        //   - Second constructor takes a Map<String, Object> object as it's argument. This is a practical way to instantiate an patient object since this is format we get after calling getData() on each QueryDocumentSnapshot object retrieved from our Firebase Firestore.

public class Patient{

    private User user;
    private String cpr;

    private String location;
    private ArrayList<String> diagnoses = new ArrayList<>();
    private ArrayList<String> medications = new ArrayList<>();

    public Patient(User user, String cpr, String location){
        this.user = user;
        this.cpr = cpr;
        this.location = location;
    }
    public Patient(Map<String, Object> map){
        Log.d("DH_DB","Map " + map.keySet().toString());
        this.user = new User();
        user.setFullName(map.get("name").toString());
        user.setUserType(map.get("userType").toString());
        this.cpr = map.get("cpr").toString();
        this.location = map.get("location").toString();
        this.diagnoses = (ArrayList<String>) map.get("diagnoses");
        this.medications = (ArrayList<String>) map.get("medications");
    }

    public void addDiagnosis(String diagnosis){
        diagnoses.add(diagnosis);
    }
    public void addMedication(String medication){
        medications.add(medication);
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }
    public String getCpr() {
        return cpr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(ArrayList<String> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public ArrayList<String> getMedications() {
        return medications;
    }
    public void setMedications(ArrayList<String> medications) {
        this.medications = medications;
    }

}
