package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {
    EditText usernameFieldInput, passwordFieldInput;
    Button signup, login;
    SharedPreferences prefs;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        Log.v("LogInActivity", "started onCreate");

        usernameFieldInput = findViewById(R.id.usernameET);
        passwordFieldInput = findViewById(R.id.passwordET);
        signup = findViewById(R.id.signupBtn);
        login = findViewById(R.id.loginBtn);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(usernameFieldInput.getText().toString(), passwordFieldInput.getText().toString());
        editor.apply();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storedPass = prefs.getString(usernameFieldInput.getText().toString(), null);
                if (storedPass != null && storedPass.equals(passwordFieldInput.getText().toString())) {
                    //if the login was a success, as in given data matches stored data, then:
                    i = new Intent(LogInActivity.this, HomePageActivity.class);
                    startActivity(i);
                }
                else if(storedPass != null && !storedPass.equals(passwordFieldInput.getText().toString())) {
                    //if the username exists, but given password doesn't match said username's respective password then:
                    Toast.makeText(LogInActivity.this, "Wrong password, please try again", Toast.LENGTH_SHORT).show();
                }
                else if(storedPass == null){
                    //if given username doesn't exist in the database then:
                    Toast.makeText(LogInActivity.this,
                            "Username not found. If you have an account, please try again",
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(LogInActivity.this,
                            "If not, create a new account in Sign up",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void LoginFunc(SharedPreferences prefs){

    }
}