package com.example.a22056_app.Models;

import java.util.ArrayList;

public class Patient {

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
    /* Måske god idé at lave en metode som kan bygge et patient object fra en et firebaseuser object.
  /*  public Patient(FirebaseUser user){

    } */
    public void addDiagnosis(String diagnosis){
        diagnoses.add(diagnosis);
    }
    public void addMedication(String medication){
        medications.add(medication);
    }
    public User getUser() {
        return user;
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
