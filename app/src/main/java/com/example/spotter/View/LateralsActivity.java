package com.example.spotter.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.example.spotter.R;

public class LateralsActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_Log";

    private EditText rightReadingText, leftReadingText, backReadingText;
    private Button returnButton, chartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laterals2);
        returnButton = findViewById(R.id.returnButton);
        chartButton=findViewById(R.id.chartButton);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Lobster, "Logged out.");
                FirebaseAuth.getInstance().signOut();
                goToHome();
                finish();
            }
        });

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Lobster, "Go to charts");
                Intent intent = new Intent(LateralsActivity.this, LineChartView.class);
                startActivity(intent);
            }
        });
    }

    private void goToHome(){

        Intent intent = new Intent(LateralsActivity.this, HomeActivity.class);
        startActivity(intent);

    }
}