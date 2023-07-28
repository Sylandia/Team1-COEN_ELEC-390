package com.example.spotter.View;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotter.Model.SensorData;
import com.example.spotter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ChartMain extends AppCompatActivity {
    EditText delay;
    Button startBtn, stopBtn, calibBtn;

    static final String Lobster = "Lobster_ChartMain";
    DatabaseReference refDatabase;
    boolean startTransfer = false;
    boolean stopTransfer = false;
    boolean calib = false;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_main);

        startBtn = findViewById(R.id.startButton);
        stopBtn = findViewById(R.id.stopButton);
        delay = findViewById(R.id.delayText);
        calibBtn = findViewById(R.id.calibrateBtn);
        //initially some btns are invisible
        startBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
        delay.setVisibility(View.INVISIBLE);

        refDatabase = FirebaseDatabase.getInstance().getReference("Flags"); // choose the correct pathing

        calibBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Calibration will take a second", Toast.LENGTH_LONG).show();
                calib = true;
                refDatabase.child("calib").setValue(calib);
                DatabaseReference calibRef = refDatabase.child("calib");
                calibRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean calibValue = snapshot.getValue(boolean.class);
                        if (!calibValue)  {
                            startBtn.setVisibility(View.VISIBLE);
                            calibBtn.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Lobster", "Calibration Unsuccessful");
                    }
                });
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (delay.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please fill in the delay", Toast.LENGTH_SHORT).show();
                } else {
                    int delayTime = Integer.parseInt(delay.getText().toString().trim());
                    startTransfer = true;
                    stopTransfer = false;

                    // Set the value directly at a specific location if you have a fixed path:
                    refDatabase.child("startRead").setValue(startTransfer);
                    DatabaseReference startRef = refDatabase.child("startRead");
                    startRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean startValue = snapshot.getValue(boolean.class);
                            if (!startValue)  {
                                startBtn.setVisibility(View.INVISIBLE);
                                stopBtn.setVisibility(View.VISIBLE);
                                //TODO: ADD VALUES FROM SENSORS TO SQL DATABASE
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Lobster", "Calibration Unsuccessful");
                        }
                    });

                    stopBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startTransfer = false;
                            stopTransfer = true;
                            refDatabase.child("stopRead").setValue(stopTransfer);
                            stopBtn.setVisibility(View.INVISIBLE);
                            calibBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

        });

    }
}