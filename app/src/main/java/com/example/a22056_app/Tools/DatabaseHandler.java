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

public class DatabaseHandler {

    private static FirebaseAuth mmAuth;
    //private static FirebaseFirestore firestore;

    public DatabaseHandler(){
        mmAuth = FirebaseAuth.getInstance();
    }
    public void login(String username, String password, Context ctx){
        mmAuth = FirebaseAuth.getInstance();
        mmAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        WeakReference<Context> contextWeakReference = new WeakReference<>(ctx);
                        if (contextWeakReference.get() == null){return;}
                        ((LoginActivity) contextWeakReference.get()).userFromLogin(task.isSuccessful(), mmAuth.getCurrentUser());
                    }
                });
    }
    public static void loginTest(String username, String password, OnCompleteListener<AuthResult> onCompleteListener){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(onCompleteListener);
    }
    public void createUser(String username, String password, Context ctx){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        WeakReference<Context> contextWeakReference = new WeakReference<>(ctx);
                        if (contextWeakReference.get() == null){return;}
                        ((SignUpActivity) contextWeakReference.get()).signUpCompletion(task.isSuccessful(), mAuth.getCurrentUser());
                    }
                });
    }
    public static void getCollection(String collectionName, OnCompleteListener<QuerySnapshot> onCompleteListener){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(collectionName).get().addOnCompleteListener(onCompleteListener);
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    public void getData(){}
    public void setData(){}

}
