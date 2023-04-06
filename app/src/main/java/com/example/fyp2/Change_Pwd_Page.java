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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class Change_Pwd_Page extends AppCompatActivity {

    private SharedPreferences sharedPreferences, settingPreferences;
    private TextInputLayout UsernameLayout, oldPwdLayout, changePwdLayout, changeConfirmPwdLayout;
    private EditText Username, oldPwd, changePwd, changeConfirmPwd;
    private Button btnChangePwd;
    private DatabaseManager dbManager;
    private TextView tvChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd_page);

        Declaration();
        darkModeChecking();
        if (!sharedPreferences.contains("LoginPeople")) {
            UsernameLayout.setVisibility(View.VISIBLE);
        }

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name;
                if (sharedPreferences.contains("LoginPeople")) {
                    name = sharedPreferences.getString("LoginPeople", null);
                } else {
                    name = Username.getText().toString();
                }

                String oldPassword = oldPwd.getText().toString();
                String newPassword = changePwd.getText().toString();
                String confirmPwd = changeConfirmPwd.getText().toString();

                if (Validation(name, oldPassword, newPassword, confirmPwd)) {
                    boolean updatePwd = dbManager.pwdUpdate(name, newPassword);
                    if (updatePwd) {
                        Toast.makeText(Change_Pwd_Page.this,
                                "Password changed successfully", Toast.LENGTH_SHORT).show();
                        Username.setText("");
                        oldPwd.setText("");
                        changePwd.setText("");
                        changeConfirmPwd.setText("");
                    }
                }
            }
        });

        oldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                oldPwdLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        changePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changePwdLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        changeConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeConfirmPwdLayout.setError(null);
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
            tvChangePwd.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private boolean Validation(String name, String oldPassword, String newPassword, String confirmPwd) {
        boolean validate = true;
        if (name.isEmpty()) {
            validate = false;
            UsernameLayout.setError("Please Enter your User Name");
        } else {
            UsernameLayout.setError(null);
        }


        if (oldPassword.isEmpty()) {
            validate = false;
            oldPwdLayout.setError("Please Enter Current Password");
        } else if (oldPassword.equals(newPassword)) {
            validate = false;
            oldPwdLayout.setError("New password cannot same with old password");
        } else if(!dbManager.changePwdRequest(name, oldPassword)){
            validate = false;
            oldPwdLayout.setError("You may enter a wrong password");
        }else {
            oldPwdLayout.setError(null);
        }

        //rPwdLayout.setErrorIconDrawable(R.drawable.mtrl_ic_error);
        //set the regular expression for password
        String pwdRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=\\S+$).{6,}$";
        if (newPassword.isEmpty()) {
            validate = false;
            changePwdLayout.setError("Please Enter a Password");
        } else if (!newPassword.matches(pwdRegex)) {
            validate = false;
            changePwdLayout.setError("Password should length at least 6 characters and contain at least one uppercase letter and digit");
        } else if (newPassword.equals(oldPassword)) {
            changePwdLayout.setError("New password cannot same with old password");
        } else {
            changePwdLayout.setError(null);
        }

        if (confirmPwd.isEmpty()) {
            validate = false;
            changeConfirmPwdLayout.setError("Please Enter Confirm Password");
        } else if (!newPassword.equals(confirmPwd)) {
            validate = false;
            changeConfirmPwdLayout.setError("Confirm Password must same with new password");
        } else {
            changeConfirmPwdLayout.setError(null);
        }
        return validate;
    }

    private void Declaration() {
        settingPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        UsernameLayout = findViewById(R.id.UsernameLayout);
        oldPwdLayout = findViewById(R.id.oldPwdLayout);
        changePwdLayout = findViewById(R.id.changePwdLayout);
        changeConfirmPwdLayout = findViewById(R.id.changeConfirmPwdLayout);
        Username = findViewById(R.id.Username);
        oldPwd = findViewById(R.id.oldPwd);
        changePwd = findViewById(R.id.changePwd);
        changeConfirmPwd = findViewById(R.id.changeConfirmPwd);
        btnChangePwd = findViewById(R.id.btnChangePwd);
        dbManager = new DatabaseManager(this);
        tvChangePwd = findViewById(R.id.tvChangePwd);
    }

    @Override
    public void onBackPressed() {
        if (!sharedPreferences.contains("LoginPeople")) {
            Intent intent = new Intent(Change_Pwd_Page.this, LoginPage.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Change_Pwd_Page.this, MainActivity.class);
            startActivity(intent);
        }
    }
}