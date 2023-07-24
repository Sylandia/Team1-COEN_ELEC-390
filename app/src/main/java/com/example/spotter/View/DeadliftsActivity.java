package com.example.spotter.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.example.spotter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DeadliftsActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_Deadlift";

    private TextView angle1x_text, angle1y_text, angle2x_text, angle2y_text, flex_text, relativeAngleX_text, relativeAngleY_text;
    private Button chartButton;
    DatabaseReference refDatabase, sensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadlifts);

        angle1x_text = findViewById(R.id.deadlift_a1x);
        angle1y_text = findViewById(R.id.deadlift_a1y);
        angle2x_text = findViewById(R.id.deadlift_a2x);
        angle2y_text = findViewById(R.id.deadlift_a2y);
        flex_text = findViewById(R.id.deadlift_flex);

        relativeAngleX_text = findViewById(R.id.deadlift_ra1);
        relativeAngleY_text = findViewById(R.id.deadlift_ra2);

        chartButton = findViewById(R.id.chartButton);


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

                Double angle1x , angle1y, angle2x, angle2y, flex, relative_a1, relative_a2;

                Map<String, Double> value = (Map<String, Double>) snapshot.getValue(true);
                angle1x = value.get("Angle1x");
                angle1y = value.get("Angle1y");
                angle2x = value.get("Angle2x");
                angle2y = value.get("Angle2y");
                flex = value.get("Flex");


                angle1x_text.setText(angle1x.toString());
                angle1y_text.setText(angle1y.toString());
                angle2x_text.setText(angle2x.toString());
                angle2y_text.setText(angle2y.toString());
                flex_text.setText(flex.toString());

                relative_a1 = CalculateRelativeAngle(angle1x, angle2x);
                if (relative_a1 > 0 && relative_a1 < 180) {
                    relativeAngleX_text.setText(relative_a1.toString());
                }
                else
                {
                    relative_a1 = CalculateRelativeAngle(angle2x, angle1x);
                    relativeAngleX_text.setText(relative_a1.toString());
                }

                relative_a2 = CalculateRelativeAngle(angle1y, angle2y);
                if (relative_a2 > 0 && relative_a2 < 180) {
                    relativeAngleY_text.setText(relative_a2.toString());
                }
                else
                {
                    relative_a2 = CalculateRelativeAngle(angle2y, angle1y);
                    relativeAngleY_text.setText(relative_a2.toString());
                }

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

    public double CalculateRelativeAngle(double imu1, double imu2){
        return (imu1-imu2);
    }

}