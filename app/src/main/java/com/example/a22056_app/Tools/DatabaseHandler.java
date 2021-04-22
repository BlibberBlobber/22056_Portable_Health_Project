package com.example.a22056_app.Tools;

import android.content.Context;

import com.example.a22056_app.Activities.LoginActivity;
import com.example.a22056_app.Activities.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

public class DatabaseHandler {

    private static FirebaseAuth mAuth;
    private static FirebaseFirestore firestore;

    public DatabaseHandler(){
        mAuth = FirebaseAuth.getInstance();

    }
    public void login(String username, String password, Context ctx){
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        WeakReference<Context> contextWeakReference = new WeakReference<>(ctx);
                        if (contextWeakReference.get() == null){return;}
                        ((LoginActivity) contextWeakReference.get()).userFromLogin(task.isSuccessful(), mAuth.getCurrentUser());
                    }
                });
    }
    public void createUser(String username, String password, Context ctx){
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

    public void getData(){}
    public void setData(){}

}
