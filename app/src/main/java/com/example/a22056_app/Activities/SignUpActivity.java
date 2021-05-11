package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.a22056_app.Tools.DatabaseHandler;
import com.example.a22056_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//   Developed with Java 1.8 . Please send bug reports to
//   Author  :  Daniel Hansen, Oliver Rasmussen, Morten Vorborg & Malin Schnack
//   Year  :  2021
//   University  :  Technical University of Denmark
//   ***********************************************************************
//   Activity for sign up of new users
public class SignUpActivity extends AppCompatActivity {

    private EditText emailView; // input field for email
    private EditText passwordView; // input field for password
    private Button signUpButton; // button for signing up new user from values entered in the fields below. After signing up, and intent object is created and passed as the argument in startActivity() to navigate back to the login activity.
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailView = findViewById(R.id.usernameView);
        passwordView = findViewById(R.id.passwordView);
        signUpButton = findViewById(R.id.signUpButton);
        mAuth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler handler = new DatabaseHandler();
                handler.createUser(emailView.getText().toString(), passwordView.getText().toString(), SignUpActivity.this);
            }
        });
    }

    public void signUpCompletion(boolean taskSuccessful, FirebaseUser user){ // completion handler for when Firebase returns a result from the attempt of creating a new user.
        if (taskSuccessful){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("User", user);
            startActivity(intent);
        }
    }
}