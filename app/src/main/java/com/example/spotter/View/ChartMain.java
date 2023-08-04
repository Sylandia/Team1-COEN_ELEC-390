package com.example.spotter.View;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    DatabaseReference sensorDatabase = FirebaseDatabase.getInstance().getReference("Sensor"); //path for sensor data
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
                flagDatabase.child("calib").setValue(calib);
                flagDatabase.child("stopRead").setValue(stopTransfer);
                DatabaseReference calibRef = flagDatabase.child("calib");
                calibRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean calibValue = snapshot.getValue(boolean.class);
                        if (!calibValue)  {
                            startBtn.setVisibility(View.VISIBLE);
                            calibBtn.setVisibility(View.INVISIBLE);
                            delay.setVisibility(View.VISIBLE);
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
                    int delayTime = Integer.parseInt(delay.getText().toString().trim()) * 1000;


                    countDown.setVisibility(View.VISIBLE);
                    new CountDownTimer(delayTime, 1000) {
                        public void onTick(long millisUntilFinished) {
                            countDown.setText("Start in: " + String.valueOf((int)millisUntilFinished/1000));
                        }

                        public void onFinish() {

                            startTransfer = true;
                            stopTransfer = false;

                            // Set the value directly at a specific location if you have a fixed path:
                            flagDatabase.child("startRead").setValue(startTransfer);
                            DatabaseReference startRef = flagDatabase.child("startRead");
                            startRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean startValue = snapshot.getValue(boolean.class);
                                    if (!startValue)  {
                                        startBtn.setVisibility(View.INVISIBLE);
                                        stopBtn.setVisibility(View.VISIBLE);
                                        sendSensorDataToDatabase();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("Lobster", "Calibration Unsuccessful");
                                }
                            });

                        }
                    }.start();


   /*                 new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, delayTime*1000); //Time in millisenconds

*/
                }
            }

        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTransfer = false;
                stopTransfer = true;
                flagDatabase.child("stopRead").setValue(stopTransfer);
                stopBtn.setVisibility(View.INVISIBLE);
                calibBtn.setVisibility(View.VISIBLE);
                delay.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void sendSensorDataToDatabase() {
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
                Log.d("Lobster","Failed to retrieve sensor value");
            }
        });
    }


}