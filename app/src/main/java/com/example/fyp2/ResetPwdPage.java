package com.example.fyp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ResetPwdPage extends AppCompatActivity {

    private TextInputLayout UsrNameRequestLayout, UsrGmailRequestLayout;
    private EditText UsrNameRequest, UsrGmailRequest;
    private Button btnPwdReset;
    private DatabaseManager dbManager;
    private String AutoCreatePwd;
    private TextView tvReset;
    private SharedPreferences settingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_page);
        dbManager = new DatabaseManager(ResetPwdPage.this);

        Declaration();

        darkModeChecking();

        btnPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usrName = UsrNameRequest.getText().toString();
                String usrEmail = UsrGmailRequest.getText().toString();

                if (Validation(usrName, usrEmail)) {

                    boolean searchAcc = dbManager.resetPwdRequest(usrName, usrEmail);
                    if (searchAcc) {
                        //call the method for auto creating a new password
                        AutoCreatePwd = PasswordReset();
                        //update the password to database
                        dbManager.pwdUpdate(usrName, AutoCreatePwd);
                        //copy the new password to the clipboard
                        ClipboardManager clipboardManager = (ClipboardManager)
                                getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("", AutoCreatePwd);
                        clipboardManager.setPrimaryClip(clipData);

                        AlertDialog.Builder successAlert = new AlertDialog.Builder(ResetPwdPage.this);
                        //set title and message for alert dialog
                        successAlert.setTitle("Password Reset Successfully");
                        successAlert.setMessage("New password is " + AutoCreatePwd + ", already copied to your clipboard");
                        //set the positive button for the alert dialog
                        successAlert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Redirect to login page
                                Intent intent = new Intent(ResetPwdPage.this, LoginPage.class);
                                startActivity(intent);
                            }
                        });
                        //set the negative button for the alert dialog
                        successAlert.setNegativeButton("Change Password", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Go change password page
                                Intent intent = new Intent(ResetPwdPage.this, Change_Pwd_Page.class);
                                startActivity(intent);
                            }
                        });
                        //unable to close the alert dialog when user click the outside screen of the dialog
                        AlertDialog myAlert = successAlert.create();
                        myAlert.setCanceledOnTouchOutside(false);
                        myAlert.show();

                    } else {
                        Toast.makeText(ResetPwdPage.this, "You may enter a wrong username or wrong email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void darkModeChecking() {
        if (settingPreferences.getBoolean("DarkMode", true)) {
            tvReset.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private boolean Validation(String userName, String userEmail) {
        boolean validate = true;

        if (userName.isEmpty()) {
            validate = false;
            UsrNameRequestLayout.setError("Please enter your username");
        } else {
            UsrNameRequestLayout.setError(null);
        }
        //set regular regression for email
        String emailRegex = "^([a-z0-9]+)@([a-z]+)\\.([a-z]{2,8})(\\.[a-z]{2,8})?";

        if (userEmail.isEmpty()) {
            validate = false;
            UsrGmailRequestLayout.setError("Please enter your gmail");
        } else if (!userEmail.matches(emailRegex)) {
            validate = false;
            UsrGmailRequestLayout.setError("Please enter a valid email");
        } else {
            UsrGmailRequestLayout.setError(null);
        }

        return validate;
    }

    private String PasswordReset() {
        int random;
        String lower_case = "abcdefghijklmnopqrstuvwxyz";
        String upper_case = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String ResetPwd = "";

        for (int i = 0; i < 2; i++) {
            ResetPwd += String.valueOf((int) (10 * Math.random()));
            random = (int) (lower_case.length() * Math.random());
            ResetPwd += String.valueOf(lower_case.charAt(random));
            random = (int) (upper_case.length() * Math.random());
            ResetPwd += String.valueOf(upper_case.charAt(random));
        }
        return ResetPwd;
    }

    private void Declaration() {
        settingPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        UsrNameRequest = findViewById(R.id.UsrNameRequest);
        UsrNameRequestLayout = findViewById(R.id.UsrNameRequestLayout);
        UsrGmailRequest = findViewById(R.id.UsrGmailRequest);
        UsrGmailRequestLayout = findViewById(R.id.UsrGmailRequestLayout);
        btnPwdReset = findViewById(R.id.btnPwdReset);
        tvReset = findViewById(R.id.tvReset);
    }
}