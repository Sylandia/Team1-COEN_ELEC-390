package com.example.spotter.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.spotter.Model.FlexSensor;
import com.example.spotter.Model.ImuSensor;
import com.google.firebase.auth.FirebaseAuth;
import com.example.spotter.R;
import com.example.spotter.Controller.DataBaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SquatActivity extends AppCompatActivity {

    private boolean timerPaused = false;

    private boolean timerRunning = false;

    private CountDownTimer countDownTimer;

    private int counter;

    static final String Lobster = "Lobster_Squat";

    private TextView angle1x_text, angle1y_text, angle2x_text, angle2y_text, flex_text, relativeAngleX_text, relativeAngleY_text;
    private TextView clockTextView;
    private Button chartButton, helpButton, startClockButton, resetClockButton;
    private DataBaseHelper db;

    DatabaseReference refDatabase;

    private View.OnClickListener helpActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(Lobster, "Go to Help");
            FragmentManager fm = getSupportFragmentManager();
            HelpFragmentSquats hp = new HelpFragmentSquats();
            hp.show(fm, "fragment_help_squat");
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats);
        db = new DataBaseHelper(SquatActivity.this);

        angle1x_text = findViewById(R.id.squat_a1x);
        angle1y_text = findViewById(R.id.squat_a1y);
        angle2x_text = findViewById(R.id.squat_a2x);
        angle2y_text = findViewById(R.id.squat_a2y);
        flex_text    = findViewById(R.id.squat_flex);
        relativeAngleX_text = findViewById(R.id.squat_ra1);
        relativeAngleY_text = findViewById(R.id.squat_ra2);

        chartButton = findViewById(R.id.chartButton);
        helpButton = findViewById(R.id.helpButton);
        //startClockButton = findViewById(R.id.startClockButton);
        //resetClockButton = findViewById(R.id.resetClockButton);
        //clockTextView = findViewById(R.id.clockTextView);

        /*startClockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    if (timerPaused) {
                        resumeTimer();
                    } else {
                        pauseTimer();
                    }
                } else {
                    startTimer();
                }
            }
        });*/

        /*resetClockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });*/



        helpButton.setOnClickListener(helpActivity);

        getSupportActionBar().setTitle("Squats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Lobster, "Go to charts");
                Intent intent = new Intent(SquatActivity.this, ChartMain.class);
                startActivity(intent);
            }
        });

        UpdateRealTimeData();
        db.getIMU();

    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateRealTimeData();
    }

    private void startTimer() {
        timerRunning = true;
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                counter++;
                clockTextView.setText(String.valueOf(counter));
            }

            public void onFinish() {
                clockTextView.setText("Stop");
                stopTimer();
            }
        }.start();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerPaused = true;
    }

    private void resumeTimer() {
        timerRunning = true;
        timerPaused = false;
        countDownTimer = new CountDownTimer((30 - counter) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                counter++;
                clockTextView.setText(String.valueOf(counter));
            }

            public void onFinish() {
                clockTextView.setText("Stop");
                stopTimer();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        timerPaused = false;
        counter = 0;
        clockTextView.setText(String.valueOf(counter));
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        timerPaused = false;
        counter = 0;
        clockTextView.setText(String.valueOf(counter));
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

    private void UpdateRealTimeData(){
        refDatabase = FirebaseDatabase.getInstance().getReference("Sensor"); // choose the correct pathing

        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // Double angle1x, angle1y, angle2x, angle2y, flex, relative_a1, relative_a2;

                Map<String, Double> value = (Map<String, Double>) snapshot.getValue(true);
                FlexSensor flex = new FlexSensor(value.get("Flex"));
                ImuSensor imu = new ImuSensor(value.get("Angle1x"),value.get("Angle1y"), value.get("Angle2x"), value.get("Angle2y") );

                angle1x_text.setText(String.valueOf(imu.getAngle1_x()));
                angle1y_text.setText(String.valueOf(imu.getAngle1_y()));
                angle2x_text.setText(String.valueOf(imu.getAngle2_x()));
                angle2y_text.setText(String.valueOf(imu.getAngle2_y()));
                flex_text.setText(String.valueOf(flex.getFlex()));
                relativeAngleX_text.setText(String.valueOf(imu.getRelative_x()));
                relativeAngleY_text.setText(String.valueOf(imu.getRelative_y()));
                db.insertSensors(flex, imu, "Squats");

//                angle1x = value.get("Angle1x");
//                angle1y = value.get("Angle1y");
//                angle2x = value.get("Angle2x");
//                angle2y = value.get("Angle2y");
//                flex = value.get("Flex");
//
//
//                angle1x_text.setText(angle1x.toString());
//                angle1y_text.setText(angle1y.toString());
//                angle2x_text.setText(angle2x.toString());
//                angle2y_text.setText(angle2y.toString());
//                flex_text.setText(flex.toString());
//
//                relative_a1 = CalculateRelativeAngle(angle1x, angle2x);
//                if (relative_a1 > 0 && relative_a1 < 180) {
//                    relativeAngleX_text.setText(relative_a1.toString());
//                }
//                else
//                {
//                    relative_a1 = CalculateRelativeAngle(angle2x, angle1x);
//                    relativeAngleX_text.setText(relative_a1.toString());
//                }
//
//                relative_a2 = CalculateRelativeAngle(angle1y, angle2y);
//                if (relative_a2 > 0 && relative_a2 < 180) {
//                    relativeAngleY_text.setText(relative_a2.toString());
//                }
//                else
//                {
//                    relative_a2 = CalculateRelativeAngle(angle2y, angle1y);
//                    relativeAngleY_text.setText(relative_a2.toString());
//                }

                Log.d(Lobster, "Value is: " + value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(Lobster, "Failed to retrieve value.");
            }
        });
    }

}