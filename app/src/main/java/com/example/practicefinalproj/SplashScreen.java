package com.example.practicefinalproj;
import android.os.Bundle; // Essential for the onCreate method to retrieve saved instance state.
import androidx.appcompat.app.AppCompatActivity; //The SplashScreen class extends AppCompatActivity, which is part of the AndroidX library, and provides compatibility features for older Android versions.

//new imports
import android.os.Handler; //The Handler is used to delay the execution of your code (i.e., transition to the Authentification activity after duration milliseconds).
import android.content.Intent; //Intent is used to start the Authentification activity.


public class SplashScreen extends AppCompatActivity {

    private static int duration = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(()->{
            Intent intent = new Intent(SplashScreen.this, Authentification.class);
            startActivity(intent);
            finish();
        },duration);
    }
}