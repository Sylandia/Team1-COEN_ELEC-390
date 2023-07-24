package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.spotter.R;

public class SquatHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_history);

        getSupportActionBar().setTitle("Squat Chart History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}