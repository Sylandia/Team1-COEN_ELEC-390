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
    Button startBtn, stopBtn;

    static final String Lobster = "Lobster_Log";
    DatabaseReference refDatabase;
    boolean startTransfer = false;
    boolean stopTransfer = false;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_main);

        startBtn = findViewById(R.id.startButton);
        stopBtn = findViewById(R.id.stopButton);
        delay = findViewById(R.id.delayText);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (delay.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please fill in the delay", Toast.LENGTH_SHORT).show();
                } else {
                    int delayTime = Integer.parseInt(delay.getText().toString().trim());
                    startTransfer = true;
                    stopTransfer = false;
                    refDatabase = FirebaseDatabase.getInstance().getReference("Flags"); // choose the correct pathing

                    // Create a SensorData object with the data you want to send
                    SensorData data = new SensorData(startTransfer, stopTransfer, delayTime);


                    // Set the value directly at a specific location if you have a fixed path:
                    refDatabase.child("startRead").setValue(startTransfer);
                    refDatabase.child("stopRead").setValue(stopTransfer);
                    refDatabase.child("delay").setValue(delayTime);

                    stopBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startTransfer = false;
                            stopTransfer = true;
                            refDatabase.child("startRead").setValue(startTransfer);
                            refDatabase.child("stopRead").setValue(stopTransfer);
                            refDatabase.child("delay").setValue(-1);
                        }
                    });
                }
            }

        });

    }
}