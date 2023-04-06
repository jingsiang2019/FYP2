package com.example.fyp2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TaskList> taskList;
    private DatabaseManager myDB;
    private SharedPreferences sharedPreferences,settingPreferences;
    private final String currentUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    public TaskAdapter(Context context, ArrayList<TaskList> taskList,DatabaseManager myDB,String currentUser) {
        this.context = context;
        this.taskList = taskList;
        this.myDB = myDB;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is used for inflating the layout (Giving a look to our rows based on our design layout)
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_task_layout,parent,false);
        TaskAdapter.ViewHolder viewHolder = new TaskAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        //Assigning the values to the view we created
        //Based on the position of the recycler view
        settingPreferences=context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        //change the colour when dark mode is selected
        if(settingPreferences.getBoolean("DarkMode",true)){
            holder.TaskCardView.setCardBackgroundColor(Color.parseColor("#000000"));
            holder.Task.setTextColor(Color.parseColor("#FFFFFF"));
            holder.DueDate.setTextColor(Color.parseColor("#FFFFFF"));
            holder.DueTime.setTextColor(Color.parseColor("#FFFFFF"));
        }
        if(!settingPreferences.contains("confirmFinish")){
            SharedPreferences.Editor editor=settingPreferences.edit();
            editor.putBoolean("confirmFinish",true);
            editor.commit();
        }

        if(holder.SubTaskRow.getChildCount()>0){
            holder.SubTaskRow.removeAllViews();
        }

        final TaskList finalTaskList = taskList.get(holder.getAdapterPosition());
        holder.Task.setText(taskList.get(holder.getAdapterPosition()).getTaskName());
        //holder.Task.setText(finalTaskList.getTaskName());
        String dueDate = finalTaskList.getDueDate();
        String dueTime = finalTaskList.getDueTime();
        if(!dueDate.isEmpty()){
            holder.DueDate.setText(dueDate);
            holder.DueDate.setVisibility(View.VISIBLE);
        }
        if(!dueTime.isEmpty()){
            holder.DueTime.setText(dueTime);
            holder.DueTime.setVisibility(View.VISIBLE);
        }
        if(finalTaskList.getMyDateTime().getTime()<System.currentTimeMillis()){
            holder.DueDate.setTextColor(Color.RED);
            holder.DueTime.setTextColor(Color.RED);
        }else if(settingPreferences.getBoolean("DarkMode",true)){
            holder.DueDate.setTextColor(Color.WHITE);
            holder.DueTime.setTextColor(Color.WHITE);
        }else{
            holder.DueDate.setTextColor(Color.BLACK);
            holder.DueTime.setTextColor(Color.BLACK);
        }

        if(finalTaskList.getTaskType()==2){
            Cursor cursor = myDB.getSubTask(finalTaskList.getTaskID(),currentUser);
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                do{
                    LayoutInflater myInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View subTaskView = myInflater.inflate(R.layout.sub_task_display,null,false);
                    CheckBox cbSubTask=(CheckBox) subTaskView.findViewById(R.id.cbSubTask);
                    TextView tvSubTask=(TextView) subTaskView.findViewById(R.id.subTask);
                    int subTaskID=cursor.getInt(0);
                    int taskID = cursor.getInt(1);
                    if(cursor.getInt(2)==1){
                        cbSubTask.setChecked(true);
                        tvSubTask.setPaintFlags(tvSubTask.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    tvSubTask.setText(cursor.getString(3));
                    //add the layout to the SubTaskRow Linear Layout
                    holder.SubTaskRow.addView(subTaskView);
                    //if dark mode is true then change text color
                    if(settingPreferences.getBoolean("DarkMode",true)){
                        tvSubTask.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    cbSubTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cbSubTask.isChecked()) {
                                tvSubTask.setPaintFlags(tvSubTask.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
                                myDB.SubTaskStatusUpdate(subTaskID,taskID,true,currentUser);
                            } else if (!cbSubTask.isChecked()) {
                                tvSubTask.setPaintFlags(tvSubTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG ));
                                myDB.SubTaskStatusUpdate(subTaskID,taskID,false,currentUser);
                            }
                        }
                    });
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        holder.cbTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.cbTask.isChecked()&&finalTaskList.getTaskType()!=1
                        &&settingPreferences.getBoolean("confirmFinish",true)){
                    //alert dialog for asking user confirmation
                    AlertDialog.Builder TaskAlert = new AlertDialog.Builder(context);
                    TaskAlert.setTitle("Already finish the task?");
                    //positive button
                    TaskAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myDB.DeleteSubTask(finalTaskList.getTaskID(),currentUser);
                            myDB.DeleteTaskImage(finalTaskList.getTaskID(),currentUser);
                            myDB.DeleteTask(finalTaskList.getTaskID(),currentUser);
                            holder.cbTask.setChecked(false);
                            TaskListUpdate();
                        }
                    });
                    //negative button
                    TaskAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            holder.cbTask.setChecked(false);
                        }
                    });
                    TaskAlert.setCancelable(false);
                    TaskAlert.show();

                }else if(holder.cbTask.isChecked()&&finalTaskList.getTaskType()==1){

                    //if complete then calculate next due date
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(finalTaskList.getMyDate());
                    switch (finalTaskList.getRepeat()){
                        case 0:
                            cal.add(Calendar.DATE,1);
                            break;
                        case 1:
                            cal.add(Calendar.DATE,7);
                            break;
                        case 2:
                            cal.add(Calendar.MONTH,1);
                            break;
                        case 3:
                            cal.add(Calendar.YEAR,1);
                            break;
                    }
                    String newDate = dateFormat.format(cal.getTime());
                    myDB.updateTaskDate(finalTaskList.getTaskID(),newDate,currentUser);
                    CreateReminder(finalTaskList.getTaskID(),finalTaskList.getTaskName(),newDate);
                    TaskListUpdate();
                    holder.cbTask.setChecked(false);

                } else if(holder.cbTask.isChecked()&&finalTaskList.getTaskType()!=1){
                    myDB.DeleteSubTask(finalTaskList.getTaskID(), currentUser);
                    myDB.DeleteTaskImage(finalTaskList.getTaskID(), currentUser);
                    myDB.DeleteTask(finalTaskList.getTaskID(), currentUser);
                    holder.cbTask.setChecked(false);
                    TaskListUpdate();
                }
            }
        });


        holder.TaskCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = context.getSharedPreferences("EditTask",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IsEdit","TaskEditing");
                editor.commit();
                Intent intent = new Intent(context,Add_Task_Page.class);
                Bundle extras = new Bundle();
                int TaskID = finalTaskList.getTaskID();
                String TaskName = finalTaskList.getTaskName();
                int TaskType = finalTaskList.getTaskType();
                int Repeat = finalTaskList.getRepeat();
                String DueDate = finalTaskList.getDueDate();
                String DueTime = finalTaskList.getDueTime();
                int Priority = finalTaskList.getPriority();
                String Comment = finalTaskList.getComment();
                int AdvanceType = finalTaskList.getAdvanceType();
                intent.putExtra("TaskID",TaskID);
                intent.putExtra("TaskName",TaskName);
                intent.putExtra("TaskType",TaskType);
                intent.putExtra("Repeat",Repeat);
                intent.putExtra("DueDate",DueDate);
                intent.putExtra("DueTime",DueTime);
                intent.putExtra("Priority",Priority);
                intent.putExtra("Comment",Comment);
                intent.putExtra("AdvanceType",AdvanceType);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //the recycler view just want to know the number of items you want displayed
        return taskList.size();
    }


    private void TaskListUpdate(){
        Cursor cursor = myDB.getAllTask(currentUser);
        taskList.clear();
        if (cursor.moveToFirst()) {
            do {
                taskList.add(new TaskList(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getInt(9)
                ));
            } while (cursor.moveToNext());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(taskList,dateComparator
                    .thenComparing(timeComparator)
                    .thenComparing(priorityComparator));
        }

        notifyDataSetChanged();
    }

    //compare the due date
    public static Comparator<TaskList> dateComparator = new Comparator<TaskList>() {
        @Override
        public int compare(TaskList taskList1, TaskList taskList2) {
            return taskList1.getMyDate().compareTo(taskList2.getMyDate());
        }
    };
    //compare the due time
    public static Comparator<TaskList> timeComparator = new Comparator<TaskList>() {
        @Override
        public int compare(TaskList taskList1, TaskList taskList2) {
            return taskList1.getMyTime().compareTo(taskList2.getMyTime());
        }
    };
    // compare the priority
    public static Comparator<TaskList> priorityComparator = new Comparator<TaskList>() {
        @Override
        public int compare(TaskList taskList1, TaskList taskList2) {
            return taskList2.getPriority()-taskList1.getPriority();
        }
    };

    private void CreateReminder(int EditTaskID,String TaskName,String myDateTime){
        String channelID = "myChannel";
        String channelName = "NotificationChannel";
        //declare the notificationChannel and notificationManager
        NotificationChannel notificationChannel;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(context,ReminderBroadcast.class);
        intent.putExtra("TaskID",EditTaskID);
        intent.putExtra("TaskName",TaskName);
        intent.putExtra("channelID","myChannel");
        PendingIntent reminderIntent = PendingIntent.getBroadcast(context,EditTaskID,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager =(AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        try {
            long resultDateTime=dateTimeFormat.parse(myDateTime).getTime();
            alarmManager.set(AlarmManager.RTC_WAKEUP,resultDateTime,reminderIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox cbTask;
        TextView Task,DueDate, DueTime;
        CardView TaskCardView;
        LinearLayout SubTaskRow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTask = itemView.findViewById(R.id.cbTask);
            Task = itemView.findViewById(R.id.Task);
            DueDate = itemView.findViewById(R.id.DueDate);
            DueTime = itemView.findViewById(R.id.DueTime);
            TaskCardView = itemView.findViewById(R.id.taskCard);
            SubTaskRow = itemView.findViewById(R.id.SubTaskRow);

        }
    }
}
