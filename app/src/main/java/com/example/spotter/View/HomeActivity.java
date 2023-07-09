package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.spotter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_Log";

    private Button squatsButton, deadliftsButton, curlsButton, lateralButton, logOut;

    private FirebaseUser user;

    private View.OnClickListener squatsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), SquatActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener deadliftsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), DeadliftsActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener curlsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), CurlsActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener lateralActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), LateralsActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Home button clicked
            FirebaseAuth.getInstance().signOut(); //added to sign out
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        squatsButton = findViewById(R.id.squatsButton);
        deadliftsButton = findViewById(R.id.deadliftsButton);
        curlsButton = findViewById(R.id.curlsButton);
        lateralButton = findViewById(R.id.lateralButton);

        squatsButton.setOnClickListener(squatsActivity);
        deadliftsButton.setOnClickListener(deadliftsActivity);
        curlsButton.setOnClickListener(curlsActivity);
        lateralButton.setOnClickListener(lateralActivity);

    }

//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // create return button

}