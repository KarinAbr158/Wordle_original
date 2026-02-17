package com.example.wordle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    EditText newUsername, newPassword, passwordConf;
    Button signupBtn;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.v("SignupActivity", "started onCreate");

        newUsername = findViewById(R.id.et_username);
        newPassword = findViewById(R.id.et_password);
        passwordConf = findViewById(R.id.et_confirm_password);
        signupBtn = findViewById(R.id.btn_signup);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = newUsername.getText().toString();
                String password = newPassword.getText().toString();
                String confirmPass = passwordConf.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String storedPass = prefs.getString(username, null);
                if(storedPass == null){
                    //if the new username doesn't exist then:
                    if(confirmPass.equals(password)){
                        editor.putString(username, password);
                        editor.apply();
                        //returns to the login page in order to log in
                        Intent i = new Intent(SignUpActivity.this, LogInActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}