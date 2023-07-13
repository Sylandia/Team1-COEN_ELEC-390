package com.example.spotter.Controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spotter.View.MainActivity;
import com.example.spotter.View.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.FirebaseKt;

public class FirebaseHelper {
    private static final String Lobster = "Lobster_Firebase";
    private FirebaseAuth mAuth;
    private Context context;

    public FirebaseHelper(Context context){

        mAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public boolean signIn(String email, String password) {

        final boolean[] flag = new boolean[1]; //for on success or failure

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { // from Firebase documentation for creating user https://firebase.google.com/docs/auth/android/start#java
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Lobster, "signInWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser(); // may need this future
                            flag[0] = true;

                        } else {
                            Log.w(Lobster, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Login failed.", Toast.LENGTH_SHORT).show();
                            flag[0] = false;
                        }
                    }
                });

        return flag[0];
    }
    public boolean signUp(String email, String password){

        final boolean[] flag = new boolean[1]; //for on success or failure

        mAuth.createUserWithEmailAndPassword(email, password) // from Firebase documentation for creating user https://firebase.google.com/docs/auth/android/start#java
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Lobster, "createUserWithEmail:success");
                            flag[0] = true;
                        }
                        else {

                            Log.w(Lobster, "createUserWithEmail:failure");//, task.getException());
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            flag[0] = false;
                        }
                    }
                });
        return flag[0];
    }

    public boolean signInCheck(){

        FirebaseUser currentUser = mAuth.getCurrentUser(); // check for sign in
        if(currentUser != null){
            return true;
        }
        return false;
    }

}
