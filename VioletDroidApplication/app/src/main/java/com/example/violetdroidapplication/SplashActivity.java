package com.example.violetdroidapplication;

/**
 * Created by aniquedavla on 11/30/16.
 */

//import Threading.Tasks;
import android.*;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

//        [Activity(Theme = "@style/AppTheme.Splash", MainLauncher = true, NoHistory = true)]
//public class SplashActivity : AppCompatActivity {
//        {
//static readonly string TAG = "X:" + typeof (SplashActivity).Name;
//
//public override void OnCreate(Bundle savedInstanceState, PersistableBundle persistentState)
//        {
//        base.OnCreate(savedInstanceState, persistentState);
//        Log.Debug(TAG, "SplashActivity.OnCreate");
//        }
//
//protected override void OnResume()
//        {
//        base.OnResume();
//
//        Task startupWork = new Task(() =>
//        {
//        Log.Debug(TAG, "Performing some startup work that takes a bit of time.");
//        Task.Delay(5000); // Simulate a bit of startup work.
//        Log.Debug(TAG, "Working in the background - important stuff.");
//        });
//
//        startupWork.ContinueWith(t =>
//        {
//        Log.Debug(TAG, "Work is finished - start Activity1.");
//        StartActivity(new Intent(Application.Context, typeof (Activity1)));
//        }, TaskScheduler.FromCurrentSynchronizationContext());
//
//        startupWork.Start();
//        }
//        }
//        }}
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity{
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}