package com.example.fyp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginPage extends AppCompatActivity {

    private TextView linkGoReset, linkGoRegister, textLogin, textNewUser;
    private Button btnLogin, btnGuestLogin;
    private TextInputLayout lUserNameLayout, lPasswordLayout;
    private EditText lUserName, lPassword;
    private CheckBox cbRememberMe;
    private SharedPreferences sharedPreferences, settingPreferences;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        //Link the view by the ID
        Declaration();
        //Remember Me Check Box Checking
        CBRememberChecking();
        //checking the dark mode
        darkModeChecking();
        //Go to Reset Page
        linkGoReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, ResetPwdPage.class);
                startActivity(intent);
            }
        });

        //Go to Registration Page
        linkGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
                startActivity(intent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usrName = lUserName.getText().toString();
                String usrPwd = lPassword.getText().toString();
                if (Validation(usrName, usrPwd)) {
                    if (dbManager.UserLogin(usrName, usrPwd)) {
                        RememberMe(usrName, usrPwd);
                        AlwaysLogin(usrName);
                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginPage.this,
                                "You may be entered a wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlwaysLogin("Guest");
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        lPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lPasswordLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void darkModeChecking() {
        if (settingPreferences.getBoolean("DarkMode", true)) {
            linkGoReset.setTextColor(Color.parseColor("#FFBB86FC"));
            linkGoRegister.setTextColor(Color.parseColor("#FFBB86FC"));
            textLogin.setTextColor(Color.parseColor("#FFFFFF"));
            textNewUser.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void RememberMe(String username, String password) {
        if (cbRememberMe.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Username", username);
            editor.putString("Password", password);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.clear(); this is used for clear all
            editor.remove("Username");
            editor.remove("Password");
            //editor.commit();
            editor.apply();
        }
    }

    private void AlwaysLogin(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LoginPeople", username);
        editor.apply();
    }

    //Remember Me Check Box Checking
    private void CBRememberChecking() {
        if (sharedPreferences.contains("Username") && sharedPreferences.contains("Password")) {
            cbRememberMe.setChecked(true);
            lUserName.setText(sharedPreferences.getString("Username", null));
            lPassword.setText(sharedPreferences.getString("Password", null));
        }

    }

    private boolean Validation(String userName, String userPassword) {
        boolean validate = true;

        if (userName.isEmpty()) {
            validate = false;
            lUserNameLayout.setError("Please Enter Your User Name");
        } else {
            lUserNameLayout.setError(null);
        }

        //regular expression for password
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=\\S+$).{6,}$";
        if (userPassword.isEmpty()) {
            validate = false;
            lPasswordLayout.setError("Please Enter Your Password");
        } else if (!userPassword.matches(passwordRegex)) {
            validate = false;
            lPasswordLayout.setError("Error Password Format");
        } else {
            lPasswordLayout.setError(null);
        }

        return validate;
    }

    private void Declaration() {
        dbManager = new DatabaseManager(LoginPage.this);
        settingPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        linkGoReset = findViewById(R.id.linkGoReset);
        linkGoRegister = findViewById(R.id.linkGoRegister);
        textLogin = findViewById(R.id.textLogin);
        textNewUser = findViewById(R.id.textNewUser);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuestLogin = findViewById(R.id.btnGuestLogin);
        lUserName = findViewById(R.id.lUserName);
        lUserNameLayout = findViewById(R.id.lUserNameLayout);
        lPassword = findViewById(R.id.lPassword);
        lPasswordLayout = findViewById(R.id.lPasswordLayout);
        cbRememberMe = findViewById(R.id.rememberMe);
    }
}