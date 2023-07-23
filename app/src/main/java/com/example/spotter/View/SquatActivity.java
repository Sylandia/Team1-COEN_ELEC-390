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

public class SquatActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_Log";

    private TextView angle1x_text, angle1y_text, angle2x_text, angle2y_text, flex_text;
    private Button chartButton;

    DatabaseReference refDatabase, sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats);

        angle1x_text = findViewById(R.id.squat_a1x);
        angle1y_text = findViewById(R.id.squat_a1y);
        angle2x_text = findViewById(R.id.squat_a2x);
        angle2y_text = findViewById(R.id.squat_a2y);
        flex_text    = findViewById(R.id.squat_flex);

        chartButton = findViewById(R.id.chartButton);

        getSupportActionBar().setTitle("Squats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Lobster, "Go to charts");
                Intent intent = new Intent(SquatActivity.this, LineChartView.class);
                startActivity(intent);
            }
        });

        refDatabase = FirebaseDatabase.getInstance().getReference("Sensor"); // choose the correct pathing

        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double angle1x, angle1y, angle2x, angle2y, flex;

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

                Log.d(Lobster, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*refDatabase.addValueEventListener(new ValueEventListener() { // to update the values from realtime database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Double angle1x, angle1y, angle2x, angle2y, flex;

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

                Log.d(Lobster, "Value is: " + value);
            }
        });*/

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