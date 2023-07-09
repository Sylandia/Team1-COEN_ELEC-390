package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.spotter.Controller.FirebaseHelper;
import com.example.spotter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    static final String Lobster = "Lobster_Log";
    Button signout, charts;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signout = findViewById(R.id.signOutButton);
        charts=findViewById(R.id.btn_charts);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user == null){
            goToLogin();
            finish();
        }

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Lobster, "Logged out.");
                FirebaseAuth.getInstance().signOut();
                goToLogin();
                finish();
            }
        });

        charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Lobster, "Go to charts");
                Intent intent = new Intent(LoginActivity.this, LineChartView.class);
                startActivity(intent);
            }
        });

    }
    private void goToLogin(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }
}