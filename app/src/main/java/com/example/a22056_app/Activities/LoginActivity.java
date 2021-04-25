package com.example.a22056_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a22056_app.R;
import com.example.a22056_app.Tools.Configuration;
import com.example.a22056_app.Tools.DataParser;
import com.example.a22056_app.Tools.DatabaseHandler;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    TextView headerTextView, unameTextView, pwordTextView;
    Button signInButton, signUpButton;
    ProgressBar progressBar;
    String system;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        system = Configuration.getConfigValue(LoginActivity.this,"system");
        headerTextView = findViewById(R.id.headerTextView);

        DataParser parser = new DataParser();
        InputStream inputStream = getResources().openRawResource(R.raw.features);
        try {
            parser.getData(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (system.equals("healthcare")){
            headerTextView.setText("Post Surgery Connect");
        }
        unameTextView = findViewById(R.id.unameTextView);
        pwordTextView = findViewById(R.id.pwordTextView);
        progressBar = findViewById(R.id.progressBar);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d("DH_LOGIN", "Username: " + unameTextView.getText().toString());
                Log.d("DH_LOGIN", "Password: " + pwordTextView.getText().toString());
                DatabaseHandler handler = new DatabaseHandler();
                handler.login(unameTextView.getText().toString(), pwordTextView.getText().toString(), LoginActivity.this);
               /* DatabaseHandler.loginTest(pwordTextView.getText().toString(), pwordTextView.getText().toString(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent;
                            if (system.equals("healthcare")) {
                                //Til patient liste
                                intent = new Intent(LoginActivity.this, PatientListActivity.class);
                            } else {
                                //Til patient detail
                                intent = new Intent(LoginActivity.this, MeasurementsActivity.class);
                            }
                            intent.putExtra("User", DatabaseHandler.getCurrentUser());
                            startActivity(intent);
                        }
                    }
                });*/
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });


    }
    public void userFromLogin(boolean taskSuccessful, FirebaseUser user) {
        if (taskSuccessful){
            progressBar.setVisibility(View.INVISIBLE);
            Intent intent;
            if (system.equals("healthcare")) {
                //Til patient liste
                intent = new Intent(this, PatientListActivity.class);
            } else {
                //Til patient detail
                intent = new Intent(this, OverviewActivity.class);
            }
            intent.putExtra("User", user);
            startActivity(intent);
        }
    }
}