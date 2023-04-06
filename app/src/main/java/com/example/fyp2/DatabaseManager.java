package com.example.fyp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ToDoListDatabase.db";
    private static final int DATABASE_VERSION = 1;

    //AccountTable
    private static final String UserAcc_TABLE = "UserAccTable";
    private static final String COLUMN_UserID = "UserID";
    private static final String COLUMN_UserName = "UserName";
    private static final String COLUMN_UserGmail = "UserGmail";
    private static final String COLUMN_UserPassword = "UserPassword";

    //TaskTable
    private static final String Task_Table = "TaskTable";
    private static final String COLUMN_TaskID = "TaskID";
    private static final String COLUMN_TaskName = "TaskName";
    private static final String COLUMN_TaskType = "TaskType";
    private static final String COLUMN_REPEAT = "Repeat";
    private static final String COLUMN_DueDate = "DueDate";
    private static final String COLUMN_DueTime = "DueTime";
    private static final String COLUMN_Priority = "Priority";
    private static final String COLUMN_Comment = "Comment";
    private static final String COLUMN_taskUserName = "UserName";
    private static final String COLUMN_AdvanceReminder = "AdvanceReminder";

    //SubTaskTable
    private static final String SubTask_Table = "SubTaskTable";
    private static final String COLUMN_SubTaskID = "SubTaskID";
    private static final String COLUMN_SubTaskPrimary = "TaskID";
    private static final String COLUMN_SubTaskCheckBox = "CheckBoxStatus";
    private static final String COLUMN_SubTaskName = "SubTaskName";
    private static final String COLUMN_subUserName = "UserName";

    //TaskImageTable
    private static final String TaskImage_Table = "TaskImage_Table";
    private static final String COLUMN_TaskImagePrimary = "TaskID";
    private static final String COLUMN_ImageURI = "ImageURI";
    private static final String COLUMN_ImageName = "ImageName";
    private static final String COLUMN_ImageData = "ImageData";
    private static final String COLUMN_imgUserName = "UserName";


    public DatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //User Account Table
        String sql1 = "CREATE TABLE " + UserAcc_TABLE + "(" +
                COLUMN_UserID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                COLUMN_UserName + " VARCHAR(50) NOT NULL ," +
                COLUMN_UserGmail + " VARCHAR(50) NOT NULL ," +
                COLUMN_UserPassword + " VARCHAR(50) NOT NULL" +
                ");";

        //Task Table
        String sql2 = "CREATE TABLE " + Task_Table + "(" +
                COLUMN_TaskID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                COLUMN_TaskName + " VARCHAR(50) NOT NULL ," +
                COLUMN_TaskType + " INTEGER NOT NULL ," +
                COLUMN_REPEAT + " INTEGER NOT NULL ," +
                COLUMN_DueDate + " VARCHAR(50) ," +
                COLUMN_DueTime + " VARCHAR(50) ," +
                COLUMN_Priority + " INTEGER NOT NULL ," +
                COLUMN_Comment + " TEXT ," +
                COLUMN_taskUserName + " VARCHAR(50) NOT NULL ," +
                COLUMN_AdvanceReminder + " INTEGER ," +
                " FOREIGN KEY (" + COLUMN_taskUserName + ") REFERENCES " + UserAcc_TABLE + "(" + COLUMN_UserName + ")" +
                ");";

        //Sub Task Table
        String sql3 = "CREATE TABLE " + SubTask_Table + "(" +
                COLUMN_SubTaskID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                COLUMN_SubTaskPrimary + " INTEGER NOT NULL ," +
                COLUMN_SubTaskCheckBox + " BOOLEAN NOT NULL ," +
                COLUMN_SubTaskName + " VARCHAR(50) NOT NULL ," +
                COLUMN_subUserName + " VARCHAR(50) NOT NULL ," +
                " FOREIGN KEY (" + COLUMN_SubTaskPrimary + ") REFERENCES " + Task_Table + "(" + COLUMN_TaskID + ") ," +
                " FOREIGN KEY (" + COLUMN_subUserName + ") REFERENCES " + UserAcc_TABLE + "(" + COLUMN_UserName + ")" +
                ");";

        //Task Image Table
        //xml understand what the progress about your system
        String sql4 = "CREATE TABLE " + TaskImage_Table + "(" +
                COLUMN_TaskImagePrimary + " INTEGER NOT NULL ," +
                COLUMN_ImageURI + " TEXT NOT NULL ," +
                COLUMN_ImageName + " TEXT NOT NULL ," +
                COLUMN_ImageData + " BLOB NOT NULL ," +
                COLUMN_imgUserName + " VARCHAR(50) NOT NULL ," +
                " FOREIGN KEY (" + COLUMN_TaskImagePrimary + ") REFERENCES " + Task_Table + "(" + COLUMN_TaskID + ") ," +
                " FOREIGN KEY (" + COLUMN_imgUserName + ") REFERENCES " + UserAcc_TABLE + "(" + COLUMN_UserName + ")" +
                ");";

        sqLiteDatabase.execSQL(sql1);
        sqLiteDatabase.execSQL(sql2);
        sqLiteDatabase.execSQL(sql3);
        sqLiteDatabase.execSQL(sql4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserAcc_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SubTask_Table);
        db.execSQL("DROP TABLE IF EXISTS " + TaskImage_Table);
        db.execSQL("DROP TABLE IF EXISTS " + Task_Table);
        onCreate(db);
    }

    //insert for user registration
    public boolean UserRegister(String usrName, String usrGmail, String usrPwd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //put the value into ContentResolver
        contentValues.put(COLUMN_UserName, usrName);
        contentValues.put(COLUMN_UserGmail, usrGmail);
        contentValues.put(COLUMN_UserPassword, usrPwd);
        //insert into the database
        long result = db.insert(UserAcc_TABLE, null, contentValues);
        if (result == -1) {
            //not successful
            return false;
        } else {
            return true;
        }
    }

    public boolean pwdUpdate(String UserName, String UsrPwd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UserPassword,UsrPwd);
        return db.update(UserAcc_TABLE,contentValues,COLUMN_UserName+"=?",new String[]{UserName})>0;
    }

    //insert task
    public boolean AddTask(String TaskName, int TaskType, int Repeat, String DueDate, String DueTime, int Priority, String Comment,String UserName,int AdvanceType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TaskName, TaskName);
        contentValues.put(COLUMN_TaskType, TaskType);
        contentValues.put(COLUMN_REPEAT, Repeat);
        contentValues.put(COLUMN_DueDate, DueDate);
        contentValues.put(COLUMN_DueTime, DueTime);
        contentValues.put(COLUMN_Priority, Priority);
        contentValues.put(COLUMN_Comment, Comment);
        contentValues.put(COLUMN_taskUserName,UserName);
        contentValues.put(COLUMN_AdvanceReminder,AdvanceType);
        long result = db.insert(Task_Table, null, contentValues);
        if (result == -1) {
            //not successful
            return false;
        } else {
            return true;
        }
    }


    public boolean TaskUpdate(int TaskID, String TaskName, int TaskType, int Repeat, String DueDate, String DueTime, int Priority, String Comment, String UserName,int AdvanceType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TaskName, TaskName);
        contentValues.put(COLUMN_TaskType, TaskType);
        contentValues.put(COLUMN_REPEAT, Repeat);
        contentValues.put(COLUMN_DueDate, DueDate);
        contentValues.put(COLUMN_DueTime, DueTime);
        contentValues.put(COLUMN_Priority, Priority);
        contentValues.put(COLUMN_Comment, Comment);
        contentValues.put(COLUMN_AdvanceReminder,AdvanceType);
        long result = db.update(Task_Table,contentValues,COLUMN_TaskID+"=? and "+COLUMN_taskUserName+"=?",new String[]{String.valueOf(TaskID),UserName});
        return result !=-1;
    }

    public boolean SubTaskStatusUpdate(int SubTaskID, int TaskID,boolean status, String UserName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SubTaskCheckBox,status);
        return db.update(SubTask_Table,contentValues,
                COLUMN_SubTaskID+"=? and "+COLUMN_SubTaskPrimary+"=? and "+COLUMN_subUserName+"=?"
                ,new String[]{String.valueOf(SubTaskID),String.valueOf(TaskID),UserName})>0;
    }

    public boolean updateTaskDate(int TaskID,String DueDate, String UserName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DueDate,DueDate);
        return db.update(Task_Table,contentValues,
                COLUMN_TaskID+"=? and "+COLUMN_taskUserName+"=?",new String[]{String.valueOf(TaskID),UserName})>0;
    }


    //insert sub task
    public boolean AddSubTask(int TaskID, Boolean cbStatus, String subTaskName, String UserName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SubTaskPrimary, TaskID);
        contentValues.put(COLUMN_SubTaskCheckBox, cbStatus);
        contentValues.put(COLUMN_SubTaskName, subTaskName);
        contentValues.put(COLUMN_subUserName, UserName);
        long result = db.insert(SubTask_Table, null, contentValues);
        return result != -1;
    }

    //insert image
    public boolean AddImg(int TaskID, String imgURI, String imgName, byte[] bytesImage, String UserName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TaskImagePrimary, TaskID);
        contentValues.put(COLUMN_ImageURI, imgURI);
        contentValues.put(COLUMN_ImageName, imgName);
        contentValues.put(COLUMN_ImageData, bytesImage);
        contentValues.put(COLUMN_imgUserName, UserName);
        long result = db.insert(TaskImage_Table, null, contentValues);
        return result != -1;
    }

    //getting newest task
    public Cursor LatestTaskID(String UserName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT MAX(" + COLUMN_TaskID + ") FROM " + Task_Table+" WHERE "+COLUMN_taskUserName+"=?",new String[]{UserName});
    }

    //getting all the task
    public Cursor getAllTask(String UserName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Task_Table+" WHERE "+COLUMN_taskUserName+"=?",new String[]{UserName});
        //return db.rawQuery( "SELECT * FROM "+Task_Table+" WHERE "+COLUMN_TaskID+"=?",new String[]{String.valueOf(4)});
    }

    //getting all the task that having the keyword
    public Cursor searchTask(String taskName,String UserName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+Task_Table + " WHERE "+COLUMN_TaskName+" LIKE ? and "+COLUMN_taskUserName+"=?",new String[]{"%"+taskName+"%",UserName});
    }

    //get specific sub task
    public Cursor getSubTask(int id,String UserName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + SubTask_Table + " WHERE " + COLUMN_SubTaskPrimary + "=? and "+COLUMN_subUserName+"=?", new String[]{String.valueOf(id),UserName});
    }

    //get specific Task Image
    public Cursor getTaskImage(int id,String UserName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TaskImage_Table + " WHERE " + COLUMN_TaskImagePrimary + "=? and "+COLUMN_imgUserName+"=?", new String[]{String.valueOf(id),UserName});
    }

    public boolean DeleteTask(int id,String UserName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Task_Table, COLUMN_TaskID + "=? and "+COLUMN_taskUserName+"=?", new String[]{String.valueOf(id),UserName}) > 0;
    }

    public boolean DeleteSubTask(int id,String UserName) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(SubTask_Table, COLUMN_SubTaskPrimary + "=? and "+COLUMN_subUserName+"=?", new String[]{String.valueOf(id),UserName});
        return result!=-1;
    }

    public boolean DeleteTaskImage(int id,String UserName) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TaskImage_Table, COLUMN_TaskImagePrimary + "=? and "+COLUMN_imgUserName+"=?", new String[]{String.valueOf(id),UserName});
        return result!=-1;
    }


    public boolean CheckUserName(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_UserName + " FROM UserAccTable WHERE UserName=?",
                new String[]{userName});
        return cursor.getCount() > 0;
    }

    public boolean CheckEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_UserGmail + " FROM UserAccTable WHERE " + COLUMN_UserGmail + "=?",
                new String[]{email});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean resetPwdRequest(String userName, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserAcc_TABLE + " WHERE " + COLUMN_UserName + "=? and " + COLUMN_UserGmail + "=?",
                new String[]{userName, email});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean UserLogin(String userName, String Password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserAcc_TABLE + " WHERE "
                + COLUMN_UserName + "=? and " + COLUMN_UserPassword + "=?", new String[]{userName, Password});
        return cursor.getCount() > 0;
    }

    public Cursor getUsrGmail(String userName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT "+COLUMN_UserGmail+" FROM "+UserAcc_TABLE+" WHERE "+COLUMN_UserName+"=?",new String[]{userName});
    }

    public boolean changePwdRequest(String userName,String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_UserName + " FROM UserAccTable WHERE UserName=? and "+COLUMN_UserPassword+"=?",
                new String[]{userName,password});
        return cursor.getCount() > 0;
    }

}
