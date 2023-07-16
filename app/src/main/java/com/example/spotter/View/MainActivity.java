package com.example.spotter.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotter.Controller.FirebaseHelper;
import com.example.spotter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    static final String Lobster = "Lobster_LogIn"; // for Log
    public EditText usernameText, passwordText;
    public Button logInButton, signUpButton;
    private FirebaseAuth mAuth; // connect to firebase
   // private FirebaseHelper fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hides menu bar
        getSupportActionBar().hide();

        usernameText = findViewById(R.id.usernameTextM);
        passwordText = findViewById(R.id.passwordTextM);
        logInButton = findViewById(R.id.logInButton);
        signUpButton = findViewById(R.id.signUpButton);
        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase
        //fb = new FirebaseHelper(MainActivity.this);
        //fb.initFirebase();


        logInButton.setOnClickListener(new View.OnClickListener() { // login button
            @Override
            public void onClick(View v) {
                if(verify()){

                    String email, password;
                    email = String.valueOf(usernameText.getText());
                    password = String.valueOf(passwordText.getText());


//                    if(fb.signIn(email, password)){
//                        Log.d(Lobster, "Account: " + email + " login.");
//                        goToLogin();
//                        finish();
//                    }
//                    else{
//
//                        Log.d(Lobster, "Failed to sign in: " + email);
//                        Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
//                    }

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() { // from Firebase documentation for creating user https://firebase.google.com/docs/auth/android/start#java
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(Lobster, "signInWithEmail:success");
                                        //FirebaseUser user = mAuth.getCurrentUser();
                                       goToLogin();
                                       finish();

                                    } else {
                                        Log.w(Lobster, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() { //Button to go to sign up
            @Override
            public void onClick(View v) {
                goToSignUp();
                finish();
            }
        });

    }
    @Override
    public void onStart(){ // make sure no one is logged in.
        super.onStart();
//        if(fb.signInCheck()){
//            Log.d(Lobster, "Already Logged in.");
//            goToLogin();
//            finish();
//        }
//
        FirebaseUser currentUser = mAuth.getCurrentUser(); // check for sign in
        if(currentUser != null){
             goToLogin();
             finish();
        }

    }

    public boolean verify(){ // to verify that fields are filled and match

        String email, password;
        boolean flag = false;
        email = String.valueOf(usernameText.getText());
        password = String.valueOf(passwordText.getText());

        //check for an email
        if(TextUtils.isEmpty(email)){

            usernameText.setError("Email required.");
            flag = true;
        }
        //check for a password
        if(TextUtils.isEmpty(password)){

            passwordText.setError("Password required.");
            flag = true;
        }
        if (flag == true){

            Toast.makeText(MainActivity.this,"Unable to login.",Toast.LENGTH_SHORT).show();
            Log.w(Lobster, "Could not login to account: " + email);
            return false;
        }
        else{

          //  Toast.makeText(MainActivity.this,"Logged in.",Toast.LENGTH_SHORT).show();
           // Log.d(Lobster, "Account: " + email + " login.");
            return true;
        }
    }
    private void goToSignUp(){
        Log.d(Lobster, "Going to Sign up");
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
    private void goToLogin(){
        Log.d(Lobster, "Going to Login");
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}