package com.example.fyp2;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

public class Add_Task_Page extends AppCompatActivity implements TimePickerFragment.InterfaceCommunicator, DatePickerFragment.InterfaceCommunicator {

    DatabaseManager dbManager;
    private int EditTaskID;
    private LinearLayout RepeatLayout, SubTaskLayout, subTask, imgLayout, AdvanceReminderLayout;
    private EditText EnterTaskName, dueDate, dueTime;
    private Spinner TaskTypeSpinner, RepeatSpinner, PrioritySpinner, AdvanceSpinner;
    private Button btnAddSubTask, btnAddComment, btnAddImg, btnImgUpload;
    private ImageButton imgBtnSpeechRecognizer, myDeleteImg;
    private TextView TextViewComment, pageStatus;
    private ImageButton dueDateIcon, dueTimeIcon;
    private SharedPreferences sharedPreferences, settingPreferences, userPreferences;
    private FloatingActionButton btnSaveToDB;
    private String TaskName, DueDate, DueTime, Comment;
    private int TaskType, Priority, Repeat, AdvanceType;
    private Toolbar addTaskToolbar;
    private String myDateTime, AdvanceDateTime;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private long resultDateTime, AdvanceResultDateTime;
    private String currentUsrName;
    private boolean AdvanceReminder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_page);

        Declaration();
        currentUsrName = userPreferences.getString("LoginPeople", null);
        TextViewComment.setMovementMethod(new ScrollingMovementMethod());

        setSupportActionBar(addTaskToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addTaskToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        if (sharedPreferences.contains("IsEdit")) {
            SetTaskContent();
            myDeleteImg.setVisibility(View.VISIBLE);
            pageStatus.setText("Edit Text");
            checkAdvance();
        }

        if (settingPreferences.getBoolean("DarkMode", true)) {
            imgBtnSpeechRecognizer.setBackgroundColor(Color.parseColor("#000000"));
            dueDateIcon.setBackgroundColor(Color.parseColor("#000000"));
            dueTimeIcon.setBackgroundColor(Color.parseColor("#000000"));
            myDeleteImg.setBackgroundColor(Color.parseColor("#FFBB86FC"));
        }

        myDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(Add_Task_Page.this);
                myAlert.setTitle("Are you sure?");
                myAlert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbManager.DeleteSubTask(EditTaskID, currentUsrName);
                        dbManager.DeleteTaskImage(EditTaskID, currentUsrName);
                        dbManager.DeleteTask(EditTaskID, currentUsrName);
                        Intent intent = new Intent(Add_Task_Page.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                myAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                myAlert.show();
            }
        });


        if (!TextViewComment.getText().toString().isEmpty()) {
            btnAddComment.setVisibility(View.GONE);
        }

        btnSaveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetTaskInfo();
                if (sharedPreferences.contains("IsEdit")) {
                    UpdateTask();
                } else {
                    AddNewTask();
                }
                if (!DueDate.isEmpty() && !DueTime.isEmpty()) {
                    String setTime = DueDate + " " + DueTime + ":00";
                    long mySetTime = 0;
                    try {
                        mySetTime = dateTimeFormat.parse(setTime).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (mySetTime > System.currentTimeMillis()) {
                        CreateReminder();
                    }
                }
                if (AdvanceReminder) {
                    int position = AdvanceSpinner.getSelectedItemPosition();
                    switch (position) {
                        case 0:
                            setAdvanceDateTime(-1);
                            break;
                        case 1:
                            setAdvanceDateTime(-3);
                            break;
                        case 2:
                            setAdvanceDateTime(-7);
                            break;
                    }
                    CreateAdvanceReminder();
                }
            }
        });

        //speech recognizer
        imgBtnSpeechRecognizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //based on the default language of current using phone
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                try {
                    startActivityForResult(intent, 10);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        //dropdown list Task Type
        TaskTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        RepeatLayout.setVisibility(View.GONE);
                        SubTaskLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        RepeatLayout.setVisibility(View.VISIBLE);
                        SubTaskLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        RepeatLayout.setVisibility(View.GONE);
                        SubTaskLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //dropdown List Repetitive
        RepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //date select from date picker
        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(view, dueDate, dueDate.getText().toString());
            }
        });
        //date select from date picker
        dueDateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(view, dueDate, dueDate.getText().toString());
            }
        });

        //if the text was changed immediately will check
        dueDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkAdvance();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //time select from time picker
        dueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(view, dueTime, dueTime.getText().toString());
            }
        });
        //time select from time picker
        dueTimeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(view, dueTime, dueTime.getText().toString());
            }
        });

        //adding a sub task
        btnAddSubTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SubTaskValidate()) {
                    AddSubTask();
                }
            }
        });

        //adding a comment
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetComment();
            }
        });
        //changing the comment
        TextViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetComment();
            }
        });
        //adding an image
        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddImage();
            }
        });

        btnImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddImage();
            }
        });


    }

    private void checkAdvance() {
        String selectedDate = dueDate.getText().toString();
        try {
            long SelectedDate = dateFormat.parse(selectedDate).getTime();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 10);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if (SelectedDate >= cal.getTimeInMillis()) {
                AdvanceReminderLayout.setVisibility(View.VISIBLE);
                AdvanceReminder = true;
            } else {
                AdvanceReminderLayout.setVisibility(View.GONE);
                AdvanceSpinner.setSelection(0);
                AdvanceReminder = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setAdvanceDateTime(int minusDate) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(DueDate));
            cal.add(Calendar.DATE, minusDate);
            AdvanceDateTime = dateFormat.format(cal.getTime()) + " 08:00:00";
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //create reminder
    private void CreateReminder() {
        String channelID = "myChannel";
        String channelName = "NotificationChannel";
        //declare the notificationChannel and notificationManager
        NotificationChannel notificationChannel;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(getApplicationContext(), ReminderBroadcast.class);
        intent.putExtra("TaskID", EditTaskID);
        intent.putExtra("TaskName", TaskName);
        intent.putExtra("channelID", "myChannel");
        PendingIntent reminderIntent = PendingIntent.getBroadcast(getApplicationContext(), EditTaskID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        myDateTime = DueDate + " " + DueTime + ":00";
        try {
            resultDateTime = dateTimeFormat.parse(myDateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, resultDateTime, reminderIntent);
    }

    //create advance reminder
    private void CreateAdvanceReminder() {
        String channelID = "myChannel";
        String channelName = "NotificationChannel";
        //declare the notificationChannel and notificationManager
        NotificationChannel notificationChannel;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(getApplicationContext(), AdvanceBroadcast.class);
        intent.putExtra("TaskID", EditTaskID);
        intent.putExtra("TaskName", TaskName);
        intent.putExtra("DueDate", DueDate);
        intent.putExtra("DueTime", DueTime);
        intent.putExtra("channelID", "myChannel");
        PendingIntent reminderIntent = PendingIntent.getBroadcast(getApplicationContext(), EditTaskID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        try {
            AdvanceResultDateTime = dateTimeFormat.parse(AdvanceDateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, AdvanceResultDateTime, reminderIntent);
    }

    //set all the content for the task
    private void SetTaskContent() {
        Bundle bundle = getIntent().getExtras();
        EditTaskID = bundle.getInt("TaskID");
        EnterTaskName.setText(bundle.getString("TaskName").trim());
        TaskTypeSpinner.setSelection(bundle.getInt("TaskType"));
        RepeatSpinner.setSelection(bundle.getInt("Repeat"));
        dueDate.setText(bundle.getString("DueDate").trim());
        dueTime.setText(bundle.getString("DueTime").trim());
        PrioritySpinner.setSelection(bundle.getInt("Priority"));
        TextViewComment.setText(bundle.getString("Comment"));
        AdvanceSpinner.setSelection(bundle.getInt("AdvanceType"));
        CommentCheck();

        Cursor subCursor = dbManager.getSubTask(EditTaskID, currentUsrName);
        subCursor.moveToFirst();
        if (subCursor.getCount() > 0) {
            do {
                AddSubTask();
                View subTaskView = subTask.getChildAt(subTask.getChildCount() - 1);
                CheckBox cbSubTask = (CheckBox) subTaskView.findViewById(R.id.subTaskCheckBox);
                EditText editText = (EditText) subTaskView.findViewById(R.id.subTaskEditText);
                if (subCursor.getInt(2) == 1) {
                    cbSubTask.setChecked(true);
                }
                editText.setText(subCursor.getString(3));
            } while (subCursor.moveToNext());
        }
        subCursor.close();

        Cursor imgCursor = dbManager.getTaskImage(EditTaskID, currentUsrName);
        imgCursor.moveToFirst();
        if (imgCursor.getCount() > 0) {
            do {
                CreateTaskImgLayout(Uri.parse(
                        imgCursor.getString(1)), imgCursor.getBlob(3), imgCursor.getString(2));
                ImgLayoutCheck();
            } while (imgCursor.moveToNext());
        }
        imgCursor.close();


    }


    //ImgLayoutChecking
    private void ImgLayoutCheck() {
        if (imgLayout.getChildCount() > 0) {
            btnImgUpload.setVisibility(View.VISIBLE);
            btnAddImg.setVisibility(View.GONE);
        } else {
            btnImgUpload.setVisibility(View.INVISIBLE);
            btnAddImg.setVisibility(View.VISIBLE);
        }
    }

    //add new task to database
    private void AddNewTask() {
        boolean validate = true;

        if (!TaskName.isEmpty()) {
            if (TaskType == 1 && DueDate.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Please set a initial date for repetitive task", Toast.LENGTH_SHORT).show();
            } else {
                boolean insertToDB1 = dbManager.AddTask
                        (TaskName, TaskType, Repeat, DueDate, DueTime, Priority, Comment, currentUsrName, AdvanceType);
                if (insertToDB1) {

                    Cursor cursor = dbManager.LatestTaskID(currentUsrName);
                    cursor.moveToFirst();
                    int foreignID = cursor.getInt(0);
                    cursor.close();
                    //used for the reminder
                    EditTaskID = foreignID;
                    if (!InsertSubTask(foreignID)) {
                        validate = false;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!InsertTaskImage(foreignID)) {
                            validate = false;
                        }
                    }

                    //if all insert to db successfully go back to main page
                    if (validate) {
                        Intent intent = new Intent(Add_Task_Page.this, MainActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Fail to add new Task", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(Add_Task_Page.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetTaskInfo() {
        TaskName = EnterTaskName.getText().toString();
        TaskType = TaskTypeSpinner.getSelectedItemPosition();
        DueDate = dueDate.getText().toString();
        DueTime = dueTime.getText().toString();
        Priority = PrioritySpinner.getSelectedItemPosition();
        Comment = TextViewComment.getText().toString();
        AdvanceType = AdvanceSpinner.getSelectedItemPosition();
        if (TaskType == 1) {
            Repeat = RepeatSpinner.getSelectedItemPosition();
        } else {
            Repeat = 0;
        }
        if (DueDate.isEmpty() && !DueTime.isEmpty()) {
            Calendar c = Calendar.getInstance();
            DueDate = dateFormat.format(c.getTime());
        } else if (!DueDate.isEmpty() && DueTime.isEmpty()) {
            DueTime = "8:0";
        }
    }

    //update the task content
    private void UpdateTask() {
        boolean validate = true;
        if (!TaskName.isEmpty()) {
            if (TaskType == 1 && DueDate.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Please set a initial date for repetitive task", Toast.LENGTH_SHORT).show();
            } else{
                boolean updateToDB = dbManager.TaskUpdate(EditTaskID, TaskName, TaskType, Repeat, DueDate, DueTime, Priority, Comment, currentUsrName, AdvanceType);
                if (updateToDB) {
                    dbManager.DeleteSubTask(EditTaskID, currentUsrName);
                    dbManager.DeleteTaskImage(EditTaskID, currentUsrName);
                    if (!InsertSubTask(EditTaskID)) {
                        validate = false;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!InsertTaskImage(EditTaskID)) {
                            validate = false;
                        }
                    }

                    //if all insert to db successfully go back to main page
                    if (validate) {
                        Intent intent = new Intent(Add_Task_Page.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fail to update Task", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(Add_Task_Page.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
        }
    }


    //insert sub task to database
    private boolean InsertSubTask(int foreignID) {
        if (subTask.getChildCount() > 0) {
            for (int i = 0; i < subTask.getChildCount(); i++) {
                boolean cbStatus;
                View subTaskView = subTask.getChildAt(i);
                CheckBox subTaskCheckBox = (CheckBox) subTaskView.findViewById(R.id.subTaskCheckBox);
                EditText subTaskEditText = (EditText) subTaskView.findViewById(R.id.subTaskEditText);
                if (subTaskCheckBox.isChecked()) {
                    cbStatus = true;
                } else {
                    cbStatus = false;
                }
                boolean result = dbManager.AddSubTask(foreignID, cbStatus, subTaskEditText.getText().toString(), currentUsrName);
                if (!result) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }


    //insert image to database
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean InsertTaskImage(int foreignID) {
        if (imgLayout.getChildCount() > 0) {
            for (int i = 0; i < imgLayout.getChildCount(); i++) {
                View imgView = imgLayout.getChildAt(i);
                ImageView taskImage = (ImageView) imgView.findViewById(R.id.imgTask);
                TextView ImgName = (TextView) imgView.findViewById(R.id.imgName);
                String imgURI = (String) taskImage.getContentDescription();
                String imgName = ImgName.getText().toString();
                String imgTempData = (String) imgView.getTag();
                //convert the data from string to byte
                byte[] imgData = Base64.getDecoder().decode(imgTempData);
                boolean result = dbManager.AddImg(foreignID, imgURI, imgName, imgData, currentUsrName);
                if (!result) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }


    //set the content of the comment
    private void SetComment() {
        //create new alertdialog object
        AlertDialog InputAlert = new AlertDialog.Builder(Add_Task_Page.this).create();
        //set the created layout as the design of this dialog
        View view = getLayoutInflater().inflate(R.layout.layout_comment_dialog, null);
        InputAlert.setView(view);
        //get the EditText and Button from that layout
        EditText contentComment = (EditText) view.findViewById(R.id.contentComment);
        Button btnSaveComment = (Button) view.findViewById(R.id.btnSaveComment);
        //set text to the edit text if having comment
        if (!TextViewComment.getText().toString().isEmpty()) {
            contentComment.setText(TextViewComment.getText().toString());
        }
        //btn save
        btnSaveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //trim is used for removing the white space before the first and the last character
                TextViewComment.setText(contentComment.getText().toString().trim());
                InputAlert.dismiss();
                CommentCheck();
            }
        });
        InputAlert.show();
    }

    private void CommentCheck() {
        if (!TextViewComment.getText().toString().isEmpty()) {
            btnAddComment.setVisibility(View.GONE);
            TextViewComment.setVisibility(View.VISIBLE);
        } else {
            btnAddComment.setVisibility(View.VISIBLE);
            TextViewComment.setVisibility(View.GONE);
        }
    }

    //Speech Recognizer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EnterTaskName.setText(result.get(0));
                }
                break;
            case 1:
                if (data != null) {
                    //https://developer.android.com/training/secure-file-sharing/retrieve-info
                    // Get the file's content URI from the incoming Intent,
                    // then query the server app to get the file's display name and size.
                    Uri ImgURI = data.getData();
                    Cursor cursor = getContentResolver().query(ImgURI,
                            null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String pictureName = cursor.getString(columnIndex);
                    cursor.close();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        SetImg(ImgURI, pictureName);
                    }


                } else {
                    //https://www.youtube.com/watch?v=-MhB-Frk0ag
                }
                break;
        }
    }

    //Add Sub Task
    private void AddSubTask() {
        View subTaskView = getLayoutInflater().inflate(R.layout.sub_task_template, null, false);
        CheckBox subTaskCheckBox = (CheckBox) subTaskView.findViewById(R.id.subTaskCheckBox);
        EditText subTaskEditText = (EditText) subTaskView.findViewById(R.id.subTaskEditText);
        ImageView imgClose = (ImageView) subTaskView.findViewById(R.id.subTaskClose);
        subTask.addView(subTaskView);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subTask.removeView(subTaskView);
            }
        });

        subTaskCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subTaskCheckBox.isChecked() & !subTaskEditText.getText().toString().isEmpty()) {
                    subTaskEditText.setPaintFlags(subTaskEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else if (!subTaskCheckBox.isChecked() & !subTaskEditText.getText().toString().isEmpty()) {
                    subTaskEditText.setPaintFlags(subTaskEditText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

            }
        });
    }

    //add the image (with the help of onActivityResult)
    private void AddImage() {
        /*Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);*/

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //set the request code = 1 then can use switch to get the result
        startActivityForResult(Intent.createChooser(intent, "Title"), 1);
    }

    //set the image to the view
    private void SetImg(Uri ImgURI, String imageName) {
        Bitmap bitmap = null;
        //Transfer to bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(Add_Task_Page.this.getContentResolver(), ImgURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
        byte[] bytesImage = byteArrayOutputStream.toByteArray();
        CreateTaskImgLayout(ImgURI, bytesImage, imageName);
        ImgLayoutCheck();

    }

    //create the image layout  and set the image
    private void CreateTaskImgLayout(Uri ImgUri, byte[] myBytesImage, String imageName) {
        View imgView = getLayoutInflater().inflate(R.layout.task_img_insert_template, null);
        ImageView imgTask = (ImageView) imgView.findViewById(R.id.imgTask);
        TextView imgName = (TextView) imgView.findViewById(R.id.imgName);
        ImageView imgCancel = (ImageView) imgView.findViewById(R.id.imgCancel);
        imgLayout.addView(imgView);

        try {
            //if having uri then set by uri else by byte
            imgTask.setImageURI(ImgUri);
        } catch (Exception e) {
            Bitmap myImage = BitmapFactory.decodeByteArray(myBytesImage, 0, myBytesImage.length);
            imgTask.setImageBitmap(myImage);
        }
        imgTask.setContentDescription(String.valueOf(ImgUri));
        imgName.setText(imageName);
        //encode the byte to a string
        String tempStorage = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            tempStorage = Base64.getEncoder().encodeToString(myBytesImage);
        }
        imgView.setTag(tempStorage);

        imgTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create new alertdialog object
                AlertDialog imgAlert = new AlertDialog.Builder(Add_Task_Page.this).create();
                //set the created layout as the design of this dialog
                View MyImgView = getLayoutInflater().inflate(R.layout.img_display, null);
                imgAlert.setView(MyImgView);
                //get the imageView from that layout
                ImageView contentComment = (ImageView) MyImgView.findViewById(R.id.imgViewDisplay);
                //set text to the edit text if having comment
                try {
                    //if having uri then set by uri else by byte
                    contentComment.setImageURI(ImgUri);
                } catch (Exception e) {
                    Bitmap myImage = BitmapFactory.decodeByteArray(myBytesImage, 0, myBytesImage.length);
                    contentComment.setImageBitmap(myImage);
                }

                imgAlert.show();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgLayout.removeView(imgView);
                ImgLayoutCheck();
            }
        });
    }


    //Ensure the sub task is all entered
    private boolean SubTaskValidate() {
        boolean validation = true;
        //if having at least one sub task then will validate else will directly return true
        if (subTask.getChildCount() > 0) {
            //get the total child and minus one for finding the latest child position
            View subTaskView = subTask.getChildAt(subTask.getChildCount() - 1);
            //get the text from the edit text of the latest sub task
            EditText edit = (EditText) subTaskView.findViewById(R.id.subTaskEditText);
            //if empty then not allow the user to create new sub task
            if (edit.getText().toString().isEmpty()) {
                validation = false;
            }
        }

        return validation;
    }

    //show the time picker and set the selected time to the edittext
    public void showTimePicker(View view, EditText GetTime, String SelectedTime) {
        DialogFragment newFragment = new TimePickerFragment("GetTime", GetTime, SelectedTime);
        newFragment.show(getSupportFragmentManager(), "TimePicker");
    }

    //show the date picker and set the selected date to the edittext
    public void showDatePicker(View view, EditText GetDate, String selectedDate) {
        DialogFragment newFragment = new DatePickerFragment("GetDate", GetDate, selectedDate);
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    private void Declaration() {
        AdvanceReminderLayout = findViewById(R.id.AdvanceReminderLayout);
        userPreferences = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("EditTask", Context.MODE_PRIVATE);
        settingPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        EnterTaskName = findViewById(R.id.EnterTName);
        AdvanceSpinner = findViewById(R.id.ddlAdvance);
        dueTime = findViewById(R.id.SetDueTime);
        dueDate = findViewById(R.id.SetDueDate);
        RepeatLayout = findViewById(R.id.RepeatLayout);
        SubTaskLayout = findViewById(R.id.SubTaskLayout);
        subTask = findViewById(R.id.subTask);
        imgLayout = findViewById(R.id.imgLayout);
        TaskTypeSpinner = findViewById(R.id.ddlTaskType);
        RepeatSpinner = findViewById(R.id.ddlRepeat);
        PrioritySpinner = findViewById(R.id.ddlPriority);
        btnAddSubTask = findViewById(R.id.btnAddSubTask);
        btnAddComment = findViewById(R.id.btnAddComment);
        btnAddImg = findViewById(R.id.btnAddImg);
        btnImgUpload = findViewById(R.id.btnImgUpload);
        imgBtnSpeechRecognizer = findViewById(R.id.imgBtnSpeechRecognizer);
        TextViewComment = findViewById(R.id.TextViewComment);
        dueDateIcon = findViewById(R.id.SetDueDateIcon);
        dueTimeIcon = findViewById(R.id.SetDueTimeIcon);
        dbManager = new DatabaseManager(Add_Task_Page.this);
        pageStatus = findViewById(R.id.pageStatus);
        myDeleteImg = findViewById(R.id.myDeleteImg);
        btnSaveToDB = findViewById(R.id.floatingBtnSave);
        addTaskToolbar = findViewById(R.id.addTaskToolbar);
    }

    //time and date picker
    @Override
    public void sendRequestCode(int Code) {

    }
/*
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Add_Task_Page.this,MainActivity.class));
    }*/
}