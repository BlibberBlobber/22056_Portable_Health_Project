package com.example.a22056_app.Tools;

import android.content.Context;
import android.util.Log;

import com.example.a22056_app.Activities.LoginActivity;
import com.example.a22056_app.Activities.SignUpActivity;
import com.example.a22056_app.Models.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.annotation.NonNull;
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
// Class for handling data retrieval, upload and user verification from Firebase project

public class DatabaseHandler {

    private static FirebaseAuth mmAuth;

    public DatabaseHandler(){
        mmAuth = FirebaseAuth.getInstance();
    }
    public void login(String username, String password, Context ctx){
        mmAuth = FirebaseAuth.getInstance(); //get connection to Authentication in Firebase project
        mmAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { // completion block for when result is returned
                        WeakReference<Context> contextWeakReference = new WeakReference<>(ctx);
                        if (contextWeakReference.get() == null){return;}
                        ((LoginActivity) contextWeakReference.get()).userFromLogin(task.isSuccessful(), mmAuth.getCurrentUser());
                    }
                });
    }

    public void createUser(String username, String password, Context ctx){
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //get connection to Authentication in Firebase project
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { // completion block for when result is returned
                        WeakReference<Context> contextWeakReference = new WeakReference<>(ctx);
                        if (contextWeakReference.get() == null){return;}
                        ((SignUpActivity) contextWeakReference.get()).signUpCompletion(task.isSuccessful(), mAuth.getCurrentUser());
                    }
                });
    }
    public static void getCollection(String collectionName, OnCompleteListener<QuerySnapshot> onCompleteListener){ // method for retrieving collections from firestore with the completion listener as parameter.
        FirebaseFirestore firestore = FirebaseFirestore.getInstance(); //get connection to Firestore in Firebase project
        firestore.collection(collectionName).get().addOnCompleteListener(onCompleteListener);
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //get connection to Authentication in Firebase project
        return mAuth.getCurrentUser();
    }
}
