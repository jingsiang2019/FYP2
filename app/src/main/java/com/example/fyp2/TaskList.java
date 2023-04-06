package com.example.fyp2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskList {
    private int TaskID;
    private String TaskName;
    private int TaskType;
    private int Repeat;
    private String DueDate;
    private String DueTime;
    private int Priority;
    private String Comment;
    private Date myDate,myTime,myDateTime;
    private int AdvanceType;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TaskList(int taskID, String taskName, int taskType, int repeat, String dueDate, String dueTime, int priority, String comment,int advanceType) {
        TaskID = taskID;
        TaskName = taskName;
        TaskType = taskType;
        Repeat = repeat;
        DueDate = dueDate;
        DueTime = dueTime;
        Priority = priority;
        Comment = comment;
        AdvanceType = advanceType;
        try {
            setDateTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDateTime() throws ParseException {
        if(!this.DueDate.isEmpty()){
            this.myDate=dateFormat.parse(DueDate);
        }else{
            String date="31/12/2055";
            this.myDate=dateFormat.parse(date);
        }
        if(!this.DueTime.isEmpty()){
            this.myTime=timeFormat.parse(DueTime);
        }else{
            String time="23:59";
            this.myTime=timeFormat.parse(time);
        }
        String dateTime = dateFormat.format(myDate)+" "+timeFormat.format(myTime)+":00";
        myDateTime=dateTimeFormat.parse(dateTime);

    }

    public Date getMyDate() {
        return myDate;
    }

    public Date getMyTime() {
        return myTime;
    }

    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public int getTaskType() {
        return TaskType;
    }

    public void setTaskType(int taskType) {
        TaskType = taskType;
    }

    public int getRepeat() {
        return Repeat;
    }

    public void setRepeat(int repeat) {
        Repeat = repeat;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getDueTime() {
        return DueTime;
    }

    public void setDueTime(String dueTime) {
        DueTime = dueTime;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Date getMyDateTime() {
        return myDateTime;
    }

    public int getAdvanceType() {
        return AdvanceType;
    }
}
