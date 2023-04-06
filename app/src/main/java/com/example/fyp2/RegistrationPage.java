package com.example.fyp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class RegistrationPage extends AppCompatActivity {

    DatabaseManager dbManager;
    private TextView linkGoLogin, tvRegister, tvAlready;
    private Button btnRegister;
    private EditText rUserName, rGmail, rPwd, rCPwd;
    private TextInputLayout rUserNameLayout, rGmailLayout, rPwdLayout, rCPwdLayout;
    private SharedPreferences settingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        dbManager = new DatabaseManager(RegistrationPage.this);
        //find view by id
        Declaration();

        darkModeChecking();

        linkGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = rUserName.getText().toString();
                String gmail = rGmail.getText().toString();
                String password = rPwd.getText().toString();
                String confirmPwd = rCPwd.getText().toString();
                if (Validation(name, gmail, password, confirmPwd)) {
                    boolean insertToDB = dbManager.UserRegister(name, gmail, password);
                    if (insertToDB) {
                        Toast.makeText(RegistrationPage.this,
                                "Registered Successfully", Toast.LENGTH_SHORT).show();
                        rUserName.setText("");
                        rGmail.setText("");
                        rPwd.setText("");
                        rCPwd.setText("");
                    }
                }
            }
        });

        rPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //remove the error icon of the textInputLayout when the user start typing
                //so password_toggle will be displayed for the user
                rPwdLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rCPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rCPwd.setError(null);
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
            linkGoLogin.setTextColor(Color.parseColor("#FFBB86FC"));
            tvRegister.setTextColor(Color.parseColor("#FFFFFF"));
            tvAlready.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }


    private boolean Validation(String name, String gmail, String password, String confirmPwd) {
        boolean validate = true;
        if (name.isEmpty()) {
            validate = false;
            rUserNameLayout.setError("Please Enter a User Name");
        } else if (dbManager.CheckUserName(name)) {
            validate = false;
            rUserNameLayout.setError("User name has been taken please enter a new user name");
        } else {
            rUserNameLayout.setError(null);
        }
        //set the regular regression for email (xyz123@gmail.com)
        String emailRegex = "^([a-z0-9]+)@([a-z]+)\\.([a-z]{2,8})(\\.[a-z]{2,8})?";
        if (gmail.isEmpty()) {
            validate = false;
            rGmailLayout.setError("Please Enter Email");
        } else if (!gmail.matches(emailRegex)) {
            validate = false;
            rGmailLayout.setError("Please Enter a Valid Email");
        } else if ((dbManager.CheckEmail(gmail))) {
            validate = false;
            rGmailLayout.setError("Email has been taken please enter a new email");
        } else {
            rGmailLayout.setError(null);
        }
        //set the regular expression for password
        String pwdRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=\\S+$).{6,}$";
        if (password.isEmpty()) {
            validate = false;
            rPwdLayout.setError("Please Enter Password");
        } else if (!password.matches(pwdRegex)) {
            validate = false;
            rPwdLayout.setError("Password should length at least 6 characters and contain at least one uppercase letter and digit");
        } else {
            rPwdLayout.setError(null);
        }

        if (confirmPwd.isEmpty()) {
            validate = false;
            rCPwdLayout.setError("Please Enter Confirm Password");
        } else if (!password.equals(confirmPwd)) {
            validate = false;
            rCPwdLayout.setError("Confirm Password must same with Password");
        } else {
            rCPwdLayout.setError(null);
        }
        return validate;
    }

    private void Declaration() {
        settingPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        dbManager = new DatabaseManager(this);
        linkGoLogin = findViewById(R.id.linkGoLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvAlready = findViewById(R.id.tvAlready);
        btnRegister = findViewById(R.id.btnRegister);
        rUserName = findViewById(R.id.rUsername);
        rGmail = findViewById(R.id.rGmail);
        rPwd = findViewById(R.id.rPwd);
        rCPwd = findViewById(R.id.rCPwd);
        rUserNameLayout = findViewById(R.id.rUsernameLayout);
        rGmailLayout = findViewById(R.id.rGmailLayout);
        rPwdLayout = findViewById(R.id.rPwdLayout);
        rCPwdLayout = findViewById(R.id.rCPwdLayout);
    }
}