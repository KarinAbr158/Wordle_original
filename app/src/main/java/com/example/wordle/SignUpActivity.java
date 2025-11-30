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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {
    EditText newUsername, newPassword, passwordConf;
    Button signupBtn;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
                String storedPass = prefs.getString(newUsername.getText().toString(), null);
                if(storedPass == null){
                    //if the new username doesn't exist then:
                    if(passwordConf.getText().toString().equals(newPassword.getText().toString())){
                        editor.putString(newUsername.getText().toString(), newPassword.getText().toString());
                        editor.apply();
                        //returns to the login page in order to log in
                        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(i);
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