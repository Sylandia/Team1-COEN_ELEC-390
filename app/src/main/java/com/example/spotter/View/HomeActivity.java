package com.example.spotter.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.example.spotter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_Log";

    private Button squatsButton, deadliftsButton, curlsButton, lateralButton;

    private FirebaseUser user;

    private View.OnClickListener squatsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(Lobster, "Go to Squats");
            Intent intent = new Intent(getBaseContext(), SquatActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener deadliftsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(Lobster, "Go to Deadlifts");
            Intent intent = new Intent(getBaseContext(), DeadliftsActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener profileActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(Lobster, "Go to Profile");
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener helpActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(Lobster, "Go to Help");
            FragmentManager fm = getSupportFragmentManager();
            HelpFragmentSquats hp = new HelpFragmentSquats();
            hp.show(fm, "fragment_help_squat");
        }
    };

//    private View.OnClickListener curlsActivity = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(getBaseContext(), CurlsActivity.class);
//            startActivity(intent);
//        }
//    };

//    private View.OnClickListener lateralActivity = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(getBaseContext(), LateralsActivity.class);
//            startActivity(intent);
//        }
//    };

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            // Home button clicked
//            FirebaseAuth.getInstance().signOut(); //added to sign out
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void LogOut() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        squatsButton = findViewById(R.id.squatsButton);
        deadliftsButton = findViewById(R.id.deadliftsButton);
        //curlsButton = findViewById(R.id.curlsButton);
        //lateralButton = findViewById(R.id.lateralButton);

        squatsButton.setOnClickListener(squatsActivity);
        deadliftsButton.setOnClickListener(deadliftsActivity);
        //curlsButton.setOnClickListener(curlsActivity);
        //lateralButton.setOnClickListener(lateralActivity);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // create return button

//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(Lobster, "Logged out.");
//                FirebaseAuth.getInstance().signOut();
//                LogOut();
//                finish();
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile_activity) {
            Log.d(Lobster, "Go to My Profile");
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_settings_activity) {
            Log.d(Lobster, "Go to Settings");
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_logOut_activity) {
                Log.d(Lobster, "Logged out.");
                FirebaseAuth.getInstance().signOut();
                LogOut();
                finish();
            return true;
        }
        return(super.onOptionsItemSelected(item));
    }

}