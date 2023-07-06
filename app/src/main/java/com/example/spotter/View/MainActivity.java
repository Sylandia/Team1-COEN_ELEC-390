package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.spotter.R;

public class MainActivity extends AppCompatActivity {

    private EditText usernameText, passwordText;
    private Button logInButton, signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        logInButton = findViewById(R.id.logInButton);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(signInActivity);
        logInButton.setOnClickListener(logInActivity);
    }

    private View.OnClickListener signInActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener logInActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
        }
    };
}