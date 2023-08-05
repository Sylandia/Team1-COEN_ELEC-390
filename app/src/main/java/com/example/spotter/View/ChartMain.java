package com.example.spotter.View;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotter.Controller.DataBaseHelper;
import com.example.spotter.Model.FlexSensor;
import com.example.spotter.Model.ImuSensor;
import com.example.spotter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ChartMain extends AppCompatActivity {
    EditText delay;
    TextView countDown;
    Button startBtn, stopBtn, calibBtn;

    static final String Lobster = "Lobster_ChartMain";
    DatabaseReference flagDatabase = FirebaseDatabase.getInstance().getReference("Flags"); //path for flag
    boolean startTransfer = false;
    boolean stopTransfer = false;
    boolean calib = false;
    private Context context = this;

    //Handler for calib timeout
    private Handler handler = new Handler();
    private Runnable calibrationTimeoutRunnable;
    private Runnable startTimeoutRunnable;
    private static final int CALIBRATION_TIMEOUT = 15000; // 15 seconds in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_main);

        startBtn = findViewById(R.id.startButton);
        stopBtn = findViewById(R.id.stopButton);
        delay = findViewById(R.id.delayText);
        calibBtn = findViewById(R.id.calibrateBtn);
        countDown = findViewById(R.id.countdownTxt);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //initially some btns are invisible
        startBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
        delay.setVisibility(View.INVISIBLE);
        countDown.setVisibility(View.INVISIBLE);

         // choose the correct pathing

        calibBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Calibration will take a second. Don't move sensors!", Toast.LENGTH_LONG).show();
                calib = true;
                stopTransfer = false;
                startTransfer = false;
                flagDatabase.child("calib").setValue(calib);
                flagDatabase.child("stopRead").setValue(stopTransfer);
                flagDatabase.child("startRead").setValue(startTransfer);
                DatabaseReference calibRef = flagDatabase.child("calib");

                // Remove the previous callback if exists to avoid multiple callbacks
                if (calibrationTimeoutRunnable != null) {
                    handler.removeCallbacks(calibrationTimeoutRunnable);
                }

                calibrationTimeoutRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // This code will be executed after 15 seconds
                        Toast.makeText(context, "Calibration failed.", Toast.LENGTH_LONG).show();
                        calibBtn.setVisibility(View.VISIBLE);
                        startBtn.setVisibility(View.INVISIBLE);
                        delay.setVisibility(View.INVISIBLE);
                    }
                };

                calibRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean calibValue = snapshot.getValue(boolean.class);
                        if (!calibValue) {
                            // Reset the previous callback
                            handler.removeCallbacks(calibrationTimeoutRunnable);

                            startBtn.setVisibility(View.VISIBLE);
                            calibBtn.setVisibility(View.INVISIBLE);
                            delay.setVisibility(View.VISIBLE);

                            // Start the 15-second timer again
                            handler.postDelayed(calibrationTimeoutRunnable, CALIBRATION_TIMEOUT);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(Lobster, "Calibration Unsuccessful");
                    }
                });

                // Start the 15-second timer for the initial calibration check
                handler.postDelayed(calibrationTimeoutRunnable, CALIBRATION_TIMEOUT);
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delay.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please fill in the delay", Toast.LENGTH_SHORT).show();
                } else {
                    int delayTime = Integer.parseInt(delay.getText().toString().trim()) * 1000;

                    countDown.setVisibility(View.VISIBLE);
                    new CountDownTimer(delayTime, 1000) {
                        public void onTick(long millisUntilFinished) {
                            countDown.setText("Start in: " + String.valueOf((int) millisUntilFinished / 1000));
                        }

                        public void onFinish() {
                            startTransfer = true;
                            stopTransfer = false;
                            flagDatabase.child("startRead").setValue(startTransfer);

                            // Remove the previous callback if exists to avoid multiple callbacks
                            if (startTimeoutRunnable != null) {
                                handler.removeCallbacks(startTimeoutRunnable);
                            }

                            startTimeoutRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    // This code will be executed after 15 seconds
                                    Toast.makeText(context, "Start failed.", Toast.LENGTH_LONG).show();
                                    startBtn.setVisibility(View.VISIBLE);
                                    stopBtn.setVisibility(View.INVISIBLE);
                                }
                            };

                            DatabaseReference startRef = flagDatabase.child("startRead");
                            startRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean startValue = snapshot.getValue(boolean.class);
                                    if (!startValue) {
                                        // Reset the previous callback
                                        handler.removeCallbacks(startTimeoutRunnable);

                                        startBtn.setVisibility(View.INVISIBLE);
                                        //stopBtn.setVisibility(View.VISIBLE);
                                        //sendSensorDataToDatabase();
                                        startAcquisition();

                                        // Start the 15-second timer again
                                        handler.postDelayed(startTimeoutRunnable, CALIBRATION_TIMEOUT);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d(Lobster, "Start Unsuccessful");
                                }
                            });

                            // Start the 15-second timer for the initial start check
                            handler.postDelayed(startTimeoutRunnable, CALIBRATION_TIMEOUT);
                        }
                    }.start();
                }
            }
        });

        /*stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTransfer = false;
                stopTransfer = true;
                flagDatabase.child("stopRead").setValue(stopTransfer);
                stopBtn.setVisibility(View.INVISIBLE);
                calibBtn.setVisibility(View.VISIBLE);
                delay.setVisibility(View.INVISIBLE);
            }
        });*/

    }

    /*private void sendSensorDataToDatabase() {
        sensorDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,Double> value = (Map<String, Double>) snapshot.getValue(true);
                FlexSensor flex = new FlexSensor(value.get("Flex"));
                ImuSensor imu = new ImuSensor(value.get("Angle1x"), value.get("Angle1y"), value.get("Angle2x"), value.get("Angle2y"));

                DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
                dataBaseHelper.insertSensors(flex, imu, "ChartMain");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Lobster,"Failed to retrieve sensor value");
            }
        });
    }*/

    public void startAcquisition (){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String contextData = sharedPreferences.getString("callingActivity", "default_value"); // Replace "key" with the context identifier used while sending and provide a default value.
        if (contextData == "squats") {
            Intent intent = new Intent(getBaseContext(), SquatActivity.class);
            startActivity(intent);
        } else if (contextData == "deadlifts") {
            Intent intent = new Intent(getBaseContext(), SquatActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(context, "Acquisition Failed.", Toast.LENGTH_LONG).show();
        }
    }


}