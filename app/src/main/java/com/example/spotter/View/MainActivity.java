package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    }
}