package com.example.spotter.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.example.spotter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class DeadliftsActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_Deadlift";

    private TextView rightReadingText, leftReadingText, backReadingText, angleText;
    private Button chartButton;
    DatabaseReference refDatabase, sensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadlifts);
        chartButton = findViewById(R.id.chartButton);
        rightReadingText = findViewById(R.id.Gyro1);
        leftReadingText = findViewById(R.id.Gyro2);
        backReadingText = findViewById(R.id.Flex);
        angleText =findViewById(R.id.Angle);

        refDatabase = FirebaseDatabase.getInstance().getReference("Sensor"); // choose the correct pathing



        getSupportActionBar().setTitle("Squats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Lobster, "Go to charts");
                Intent intent = new Intent(DeadliftsActivity.this, LineChartView.class);
                startActivity(intent);
            }
        });
        refDatabase.addValueEventListener(new ValueEventListener() { // to update the values from realtime database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Double gyro1 , gyro2, flex, angle;

                Map<String, Double> value = (Map<String, Double>) snapshot.getValue(true);
                gyro1 = value.get("Angle1x");
                gyro2 = value.get("Angle1y");
                flex = value.get("Angle2x");
                angle = value.get("Angle2y");


                rightReadingText.setText(gyro1.toString());
                leftReadingText.setText(gyro2.toString());
                backReadingText.setText(flex.toString());
                angleText.setText(angle.toString());

                Log.d(Lobster, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(Lobster, "Failed to retrieve value.");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Home button clicked
            FirebaseAuth.getInstance().signOut(); //added to sign out
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}