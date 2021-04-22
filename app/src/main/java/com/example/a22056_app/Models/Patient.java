package com.example.a22056_app.Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

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
       // user.setPassword(map.get("password").toString());
       // user.setUserId((int) map.get("userid"));
       // user.setUsername(map.get("username").toString());
        user.setUserType(map.get("userType").toString());
        this.cpr = map.get("cpr").toString();
        this.location = map.get("location").toString();
        this.diagnoses = (ArrayList<String>) map.get("diagnoses");
        this.medications = (ArrayList<String>) map.get("medications");
    }
    /* Måske god idé at lave en metode som kan bygge et patient object fra en et firebaseuser object.
  /*  public Patient(FirebaseUser user){

    } */
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
