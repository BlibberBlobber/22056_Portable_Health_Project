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

public class SignUpActivity extends AppCompatActivity {

    private EditText emailView;
    private EditText passwordView;
    private Button signUpButton;
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

    public void signUpCompletion(boolean taskSuccessful, FirebaseUser user){
        if (taskSuccessful){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("User", user);
            startActivity(intent);
        }
    }
}