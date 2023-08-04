package com.example.spotter.Controller;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class NotificationHelper extends Application{
    static final String Lobster = "Lobster_NoteHelper";
    public static final String SQUAT = "squat";
    public static final String DEADLIFT = "deadlift";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels(){ // Create the channels for the notifications

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // Check to make sure that you are using valid SDK version

            //Channels
            NotificationChannel squatNote = new NotificationChannel(SQUAT, "Squat", NotificationManager.IMPORTANCE_DEFAULT);
            squatNote.setDescription("This is for setting the notifications of the squat page.");
            squatNote.setShowBadge(true);
            NotificationChannel deadliftNote = new NotificationChannel(DEADLIFT, "Deadlift", NotificationManager.IMPORTANCE_DEFAULT);
            deadliftNote.setDescription("This is for setting the notifications of the deadlift page.");

            //Setup Channels
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(squatNote);manager.createNotificationChannel(deadliftNote);

            Log.d(Lobster, "Channels have been created");
        }
    }

}
