package com.example.fyp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.widget.Toolbar;

public class Setting extends AppCompatActivity {
    private Switch btnDarkMode,btnConfirmFinish;
    private SharedPreferences settingPreferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Declaration();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);



        if(settingPreferences.getBoolean("DarkMode",true)){
            btnDarkMode.setChecked(true);
        }else{
            btnDarkMode.setChecked(false);
        }

        btnDarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnDarkMode.isChecked()){
                    SharedPreferences.Editor editor=settingPreferences.edit();
                    editor.putBoolean("DarkMode",true);
                    editor.commit();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    SharedPreferences.Editor editor=settingPreferences.edit();
                    editor.putBoolean("DarkMode",false);
                    editor.commit();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        if(settingPreferences.getBoolean("confirmFinish",true)){
            btnConfirmFinish.setChecked(true);
        }else{
            btnConfirmFinish.setChecked(false);
        }

        btnConfirmFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnConfirmFinish.isChecked()){
                    SharedPreferences.Editor editor=settingPreferences.edit();
                    editor.putBoolean("confirmFinish",true);
                    editor.commit();
                }else{
                    SharedPreferences.Editor editor=settingPreferences.edit();
                    editor.putBoolean("confirmFinish",false);
                    editor.commit();
                }
            }
        });
    }

    private void Declaration() {
        toolbar = findViewById(R.id.toolbar);
        settingPreferences=getSharedPreferences("Setting", Context.MODE_PRIVATE);
        btnDarkMode=findViewById(R.id.BtnDarkMode);
        btnConfirmFinish=findViewById(R.id.BtnConfirmFinish);
    }
}