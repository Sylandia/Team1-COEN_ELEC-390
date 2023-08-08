package com.example.spotter.View;

import static com.example.spotter.Controller.NotificationHelper.SQUAT;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;


import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.spotter.Model.FlexSensor;
import com.example.spotter.Model.ImuSensor;
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

    private TextView flex_text, relativeAngleX_text, flexWarning, imuWarning;
    private TextView clockTextView;
    private Button chartButton, helpButton, stopAcqBtn;
    private DataBaseHelper db;
    private NotificationManagerCompat notificationManager;
    Context context = this;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean stopAcq;

    DatabaseReference sensorDatabase = FirebaseDatabase.getInstance().getReference("Sensor"); // choose the correct pathing
    DatabaseReference flagDatabase = FirebaseDatabase.getInstance().getReference("Flags"); //path for flag

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
        flex_text = findViewById(R.id.squat_flex);
        relativeAngleX_text = findViewById(R.id.squat_ra1);
        chartButton = findViewById(R.id.chartButton);
        helpButton = findViewById(R.id.helpButton);
        stopAcqBtn = findViewById(R.id.stopAcqBtn);
        chartButton.setVisibility(View.INVISIBLE);

        //Warnings
        imuWarning = findViewById(R.id.imuWarning);
        flexWarning = findViewById(R.id.flexWarning);

        //Notification
        notificationManager =  NotificationManagerCompat.from(this);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Delete Database if needed
        //db.DeleteDatabase(this);


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
                int id = sharedPreferences.getInt("id", -1);
                if(id != -1) {
                    editor.putInt("id_chart", id);
                } else {
                    Log.w(Lobster, "Failed to get id for chart view.");
                }
                Intent intent = new Intent(SquatActivity.this, LineChartView.class);
                startActivity(intent);
            }
        });

        stopAcqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAcqn();
                stopAcq = true;
                stopAcqBtn.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //UpdateRealTimeData();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        stopAcq = false;
        UpdateRealTimeData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Home button clicked
            if (!stopAcq){
                stopAcqn();
            }
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateRealTimeData() {

        sensorDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Double> value = (Map<String, Double>) snapshot.getValue(true);
                FlexSensor flex = new FlexSensor(value.get("Flex"));
                ImuSensor imu = new ImuSensor(value.get("Angle1x"), value.get("Angle1y"), value.get("Angle2x"), value.get("Angle2y"));

                squatNotification(imu, flex);

                flex_text.setText(String.valueOf(flex.getFlex()));
                relativeAngleX_text.setText(String.valueOf(imu.getRelative_x()));
                updateWarning(imu.getRelative_x(),flex.getFlex());

                boolean isDatabaseEmpty = db.isDatabaseEmpty();
                if (isDatabaseEmpty) {
                    // Database is empty
                    editor.putInt("id", 1); // Replace "key" with your context identifier and "value" with the actual data to be passed.
                    editor.apply();
                    db.insertSensors(flex, imu, "Squats", 1);
                } else {
                    //Database is not empty
                    int id = sharedPreferences.getInt("id", -1);
                    if (id != -1) {
                        db.insertSensors(flex, imu, "Squats", id);
                    }
                    else {
                        Toast.makeText(context, "Storing sensor data into database failed", Toast.LENGTH_LONG).show();
                    }
                }


                //Log.d(Lobster, "Value is: " + value);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(Lobster, "Failed to retrieve value.");
            }
        });
    }
    public void squatNotification(ImuSensor i, FlexSensor f) { // have to have notifications enabled

        if (f.getFlex() > 7.99) {
            Notification notification = new NotificationCompat.Builder(this , SQUAT)
                    .setSmallIcon(R.drawable.error_notification)
                    .setContentTitle("Squat Error")
                    .setContentText("Bending too much")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();
            notificationManager.notify(0, notification);
            Log.e(Lobster, "Notification Created");
        }else{
            //do nothing
        }
    }

    private void stopAcqn(){
        if (!stopAcq) {
            flagDatabase.child("stopRead").setValue(true);
            chartButton.setVisibility(View.VISIBLE);
            int id = sharedPreferences.getInt("id", -1);
            if(id != -1) {
                editor.putInt("id_chart", id);
                id++; //increment id for next acq
                editor.putInt("id", id);
                editor.apply();
            } else {
                Log.w(Lobster, "Failed to increment id value.");
            }
        }
    }

    private void updateWarning (double imu, double flex){
        if (imu > 110) {
            imuWarning.setText("Warning: Squat is much too deep");
        }
        else if (imu > 100) {
            imuWarning.setText("Caution: Squat is deep");
        }
        else if (imu > 80) {
            imuWarning.setText("Great Squat");
        }
        else {
            imuWarning.setText("");
        }
        if (flex > -5){
            flexWarning.setText("Back is bent. Adjust form");
        } else if(flex > -10) {
            flexWarning.setText("Back is slightly bent");
        }
        else {
            flexWarning.setText("Excellent");
        }
    }

}