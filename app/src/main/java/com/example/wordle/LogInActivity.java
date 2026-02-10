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
    Button signup, login, homepage;
    SharedPreferences prefs;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        Log.v("LogInActivity", "started onCreate");

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        usernameFieldInput = findViewById(R.id.usernameET);
        passwordFieldInput = findViewById(R.id.passwordET);
        signup = findViewById(R.id.signupBtn);
        login = findViewById(R.id.loginBtn);
        homepage = findViewById(R.id.cheatBtn);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(LogInActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameFieldInput.getText().toString();
                String password = passwordFieldInput.getText().toString();

                if(username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String storedPass = prefs.getString(username, null);
                if (storedPass != null && storedPass.equals(password)) {
                    //if the login was a success, as in given data matches stored data, then:
                    prefs.edit().putString("current_user", username).apply();
                    i = new Intent(LogInActivity.this, HomePageActivity.class);
                    startActivity(i);
                }
                else if(storedPass != null && !storedPass.equals(password)) {
                    //if the username exists, but given password doesn't match said username's respective password then:
                    Toast.makeText(LogInActivity.this, "Wrong password, please try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    //if given username doesn't exist in the database then:
                    Toast.makeText(LogInActivity.this,
                            "Username not found. Please try again or create a new account.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}