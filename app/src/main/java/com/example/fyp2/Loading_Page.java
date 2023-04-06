package com.example.fyp2;

import static com.example.fyp2.R.drawable.ic_baseline_check_box_24;
import static com.example.fyp2.R.drawable.ic_launcher_background;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

public class Loading_Page extends AppCompatActivity {

    private SharedPreferences sharedPreferences, settingPreferences;
    private ImageView appImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        Declaration();

        appImg.setImageDrawable(getDrawable(ic_baseline_check_box_24));

        if (!settingPreferences.contains("DarkMode")) {
            SharedPreferences.Editor editor = settingPreferences.edit();
            editor.putBoolean("DarkMode", false);
            editor.commit();
        } else {
            if (settingPreferences.getBoolean("DarkMode", true)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1000);  //Delay of 1 seconds
                } catch (Exception e) {

                } finally {
                    if (sharedPreferences.contains("LoginPeople")) {
                        startActivity(new Intent(Loading_Page.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(Loading_Page.this, LoginPage.class));
                    }
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

    private void Declaration() {
        settingPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        appImg = findViewById(R.id.appImg);
    }
}