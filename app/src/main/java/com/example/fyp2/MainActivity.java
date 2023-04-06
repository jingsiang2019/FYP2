package com.example.fyp2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DatabaseManager dbManager;
    private ArrayList<TaskList> taskList;
    private SharedPreferences sharedPreferences, AddTaskPreferences,settingPreferences;
    private RecyclerView TaskRecyclerView;
    private TaskAdapter myTaskAdapter;
    private FloatingActionButton myAddBtn;
    private EditText TaskSearch;
    private ImageButton btnImgSearch;
    private boolean Searching;
    private String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Declaration();

        currentUser=sharedPreferences.getString("LoginPeople",null);

        View headerView = navigationView.getHeaderView(0);
        TextView myUser = headerView.findViewById(R.id.myUser);
        TextView myUserAcc = headerView.findViewById(R.id.myUserGmail);
        myUser.setText(currentUser);
        if(currentUser.equals("Guest")){
            myUserAcc.setVisibility(View.INVISIBLE);
        }else{
            Cursor cursor=dbManager.getUsrGmail(currentUser);
            cursor.moveToFirst();
            myUserAcc.setText(cursor.getString(0));
        }

        if(settingPreferences.getBoolean("DarkMode",true)){
            TaskRecyclerView.setBackgroundColor(Color.parseColor("#1a1a1a"));
        }


        hideKeyBoard();

        //remove the title of the tool bar
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,
                drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                hideKeyBoard();
                super.onDrawerStateChanged(newState);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_screenTime:
                        Intent intent1 = new Intent(MainActivity.this, ScreenTimeDisplay.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_focusMode:
                        //startActivity(new Intent(MainActivity.this,FocusMode.class));
                        Toast.makeText(MainActivity.this,"Sorry the functions still having problem",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_changePwd:
                        if(!currentUser.equals("Guest")){
                            startActivity(new Intent(MainActivity.this,Change_Pwd_Page.class));
                        }else{
                            Toast.makeText(getApplicationContext(),"Guest cannot change password",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_setting:
                        Intent intent4 = new Intent(MainActivity.this,Setting.class);
                        startActivity(intent4);
                        break;
                    case R.id.nav_logout:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("LoginPeople");
                        editor.apply();
                        Intent intent5 = new Intent(MainActivity.this, LoginPage.class);
                        startActivity(intent5);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        //get all the task from the database and store to the array list
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setUpTaskList();
        }
        TaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myTaskAdapter = new TaskAdapter(MainActivity.this, taskList, dbManager,currentUser);
        TaskRecyclerView.setAdapter(myTaskAdapter);
        myTaskAdapter.notifyDataSetChanged();

        myAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddTaskPreferences.contains("IsEdit")) {
                    SharedPreferences.Editor editor = AddTaskPreferences.edit();
                    editor.remove("IsEdit");
                    editor.apply();
                }
                Intent intent = new Intent(MainActivity.this, Add_Task_Page.class);
                startActivity(intent);
            }
        });

        btnImgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard();
                //search specific task
                SearchTask();
            }
        });

        //when scrolling the recyclerview then remove the search edit text's focus
        TaskRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                hideKeyBoard();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        TaskSearch.clearFocus();
    }

    private void Declaration() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        settingPreferences=getSharedPreferences("Setting", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        AddTaskPreferences = getSharedPreferences("EditTask", Context.MODE_PRIVATE);
        dbManager = new DatabaseManager(this);
        taskList = new ArrayList<>();
        TaskRecyclerView = findViewById(R.id.TaskRecyclerView);
        myAddBtn = findViewById(R.id.floatingBtnAdd);
        TaskSearch = findViewById(R.id.TaskSearch);
        btnImgSearch = findViewById(R.id.btnImgSearch);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpTaskList() {
        Cursor cursor = dbManager.getAllTask(currentUser);
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

        Collections.sort(taskList,dateComparator
                .thenComparing(timeComparator)
                .thenComparing(priorityComparator));
        /*
        if(taskList.size()>1){
            GeneticAlgo ga= new GeneticAlgo(taskList);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Population population = new Population(20,taskList).sortByFitness();
                do{
                    if(population.getSchedules().get(0).getFitness()==1.0){
                        taskList=ga.getTaskList();
                    }
                    population=ga.evolve(population).sortByFitness();
                }while (population.getSchedules().get(0).getFitness()!=1.0);
                if(taskList.get(0).getDueDate().isEmpty()){
                    Collections.reverse(taskList);
                }
            }
        }*/
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



    @SuppressLint("NotifyDataSetChanged")
    private void SearchTask() {
        Cursor cursor = dbManager.searchTask(TaskSearch.getText().toString().trim(),currentUser);
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
        myTaskAdapter.notifyDataSetChanged();
        Searching = true;
    }




    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (Searching) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setUpTaskList();
            }
            myTaskAdapter.notifyDataSetChanged();
            TaskSearch.setText("");
            Searching = false;
        } else {
            //go to login page
            startActivity(new Intent(MainActivity.this, LoginPage.class));
            finish();
        }
    }
}