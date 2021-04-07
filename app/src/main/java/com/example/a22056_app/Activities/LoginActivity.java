package com.example.a22056_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a22056_app.Tools.Configuration;
import com.example.a22056_app.Tools.DatabaseHandler;
import com.example.a22056_app.R;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView unameTextView, pwordTextView;
    Button signInButton, signUpButton;
    String system;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        system = Configuration.getConfigValue(LoginActivity.this,"system");
        unameTextView = findViewById(R.id.unameTextView);
        pwordTextView = findViewById(R.id.pwordTextView);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DH_LOGIN", "Username: " + unameTextView.getText().toString());
                Log.d("DH_LOGIN", "Password: " + pwordTextView.getText().toString());
                DatabaseHandler handler = new DatabaseHandler();
                handler.login(unameTextView.getText().toString(), pwordTextView.getText().toString(), LoginActivity.this);
            }
        });


    }
    public void userFromLogin(boolean taskSuccessful, FirebaseUser user) {
        if (taskSuccessful){
            Intent intent;
            if (system.equals("healthcare")) {
                //Til patient liste
                intent = new Intent(this, PatientListActivity.class);
            } else {
                //Til patient detail
                intent = new Intent(this, MeasurementsActivity.class);
            }
            intent.putExtra("User", user);
            startActivity(intent);
        }
    }
}