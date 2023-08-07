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
    DatabaseReference calibRef = flagDatabase.child("calib");
    DatabaseReference startRef = flagDatabase.child("startRead");
    boolean startTransfer = false;
    boolean stopTransfer = false;
    boolean startButtonPressed = false;
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

                // Set calib and startTransfer to false and update the database
                calib = true;
                startTransfer = false;
                flagDatabase.child("calib").setValue(calib);
                flagDatabase.child("startRead").setValue(startTransfer);

                // Start the 15-second timer for the initial calibration check
                handler.postDelayed(calibrationTimeoutRunnable, CALIBRATION_TIMEOUT);

                // Start polling the database for changes in the calib value
                startPollingCalibValue();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delay.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please fill in the delay", Toast.LENGTH_SHORT).show();
                } else {
                    int delayTime = Integer.parseInt(delay.getText().toString().trim()) * 1000;

                    if (startButtonPressed) {
                        flagDatabase.child("startRead").setValue(true);
                        startPollingStartValue();
                    } else {
                        startButtonPressed = true;

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
                                        startPollingStartValue();
                                        if (startTransfer) {
                                            Toast.makeText(context, "Start failed.", Toast.LENGTH_LONG).show();
                                            startBtn.setVisibility(View.VISIBLE);
                                        }
                                    }
                                };
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
        if (contextData.equals("squats")) {
            Intent intent = new Intent(ChartMain.this, SquatActivity.class);
            startActivity(intent);
        } else if (contextData.equals("deadlifts")) {
            Intent intent = new Intent(ChartMain.this, DeadliftsActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(context, "Acquisition Failed.", Toast.LENGTH_LONG).show();
        }
    }

    // Function to start polling the database for changes in calib value
    private void startPollingCalibValue() {
        calibRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                calib = snapshot.getValue(boolean.class);
                if (!calib) {
                    // Calibration successful
                    handler.removeCallbacks(calibrationTimeoutRunnable);
                    startBtn.setVisibility(View.VISIBLE);
                    calibBtn.setVisibility(View.INVISIBLE);
                    delay.setVisibility(View.VISIBLE);
                } else {
                    // Calibration failed after 15 seconds
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (calib) {
                                Toast.makeText(context, "Calibration failed.", Toast.LENGTH_LONG).show();
                                calibBtn.setVisibility(View.VISIBLE);
                                startBtn.setVisibility(View.INVISIBLE);
                                delay.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, CALIBRATION_TIMEOUT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Lobster, "Failed to get calib from DB");
            }
        });
    }

    private void startPollingStartValue() {
        startRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                startTransfer = snapshot.getValue(boolean.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Lobster, "Failed to get startRead from DB");
            }
        });

    }


}