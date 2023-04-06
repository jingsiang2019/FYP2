package com.example.fyp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.LabelFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScreenTimeDisplay extends AppCompatActivity {

    private UsageStatsManager mUsageStatsManager;
    private List<ScreenTimeList> myScreenTimeList;
    private TextView currentDisplayDate;
    private ImageButton left_icon, right_icon;
    private Calendar calendar;
    private int dateCount = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private BarChart barChart;
    private LinearLayout TimeUsedDisplay;
    private int confirm=0;


    @SuppressLint("ServiceCast")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_time_display);

        Declaration();
        getAppUsage();
        getSelectedDate(dateCount);
        disableButtonRight();
        CreateBar();
        left_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCount--;
                getAppUsage();
                getSelectedDate(dateCount);
                if(myScreenTimeList.size()>0){
                    barChart.clear();
                    TimeUsedDisplay.removeAllViews();
                    CreateBar();
                } else{
                    dateCount++;
                    getAppUsage();
                    getSelectedDate(dateCount);
                }
            }
        });

        right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateCount++;
                getAppUsage();
                getSelectedDate(dateCount);
                barChart.clear();
                TimeUsedDisplay.removeAllViews();
                CreateBar();
            }
        });

    }

    private void getAppUsage() {
        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        //call the getUsageStatistic method and set the interval type as a day
        List<UsageStats> usageStatsList = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            usageStatsList = getUsageStatistic();
        }
        //sorting, the most using app will be moved up
        Collections.sort(usageStatsList, new TotalScreenTimeComparator());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            updateAppsList(usageStatsList);
        }

    }

    private void getSelectedDate(int count) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, count);
        String selectedDate = dateFormat.format(calendar.getTime());
        currentDisplayDate.setText(selectedDate);
        disableButtonRight();
        disableButtonLeft();
    }

    private void disableButtonRight() {
        if (dateCount == 0) {
            right_icon.setEnabled(false);
        } else {
            right_icon.setEnabled(true);
        }
    }

    private void disableButtonLeft() {
        if (myScreenTimeList.size() == 0 ) {
            left_icon.setEnabled(false);
        } else {
            left_icon.setEnabled(true);
        }
    }

    private void Declaration() {
        myScreenTimeList = new ArrayList<>();
        left_icon = findViewById(R.id.left_icon);
        right_icon = findViewById(R.id.right_icon);
        currentDisplayDate = findViewById(R.id.currentDisplayDate);
        barChart = findViewById(R.id.barChart);
        TimeUsedDisplay=findViewById(R.id.TimeUsedDisplay);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<UsageStats> getUsageStatistic() {
        //begin date
        Calendar beginDate = Calendar.getInstance();
        beginDate.add(Calendar.DATE, dateCount);
        beginDate.set(Calendar.HOUR_OF_DAY, 0);
        beginDate.set(Calendar.MINUTE, 0);
        beginDate.set(Calendar.SECOND, 0);
        beginDate.set(Calendar.MILLISECOND, 0);
        Calendar endDate;
        if (dateFormat.format(beginDate.getTime()).equals(dateFormat.format(Calendar.getInstance().getTime()))) {
            endDate = Calendar.getInstance();
        } else {
            endDate = Calendar.getInstance();
            endDate.add(Calendar.DATE, dateCount);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
        }

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginDate.getTimeInMillis(), endDate.getTimeInMillis())
                .stream()
                .filter(usageStats -> usageStats.getTotalTimeInForeground() > 5000
                        && usageStats.getLastTimeUsed() > beginDate.getTimeInMillis()
                        && usageStats.getLastTimeUsed() < endDate.getTimeInMillis())
                .collect(Collectors.toList());

        if (queryUsageStats.size() == 0 && confirm==0) {
            AlertDialog.Builder myAlert=new AlertDialog.Builder(ScreenTimeDisplay.this);
            myAlert.setTitle("Permission Request");
            myAlert.setMessage("Please allow the access to apps usage");
            myAlert.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(ScreenTimeDisplay.this,MainActivity.class));
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
            myAlert.setNegativeButton("Block", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(ScreenTimeDisplay.this,MainActivity.class));
                }
            });
            myAlert.show();

        }else{
            confirm++;
        }
        return queryUsageStats;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void updateAppsList(List<UsageStats> usageStatsList) {
        myScreenTimeList.clear();
        for (int i = 0; i < usageStatsList.size(); i++) {
            Drawable appIcon;
            try {
                appIcon = getPackageManager().getApplicationIcon(usageStatsList.get(i).getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                appIcon = getDrawable(R.drawable.ic_launcher_background);
            }
            ApplicationInfo appInfo = null;
            try {
                appInfo = getPackageManager().getApplicationInfo(usageStatsList.get(i).getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException foundException) {
                foundException.printStackTrace();
            }
            String appName = (String) getPackageManager().getApplicationLabel(appInfo);
            ScreenTimeList screenTimeData = new ScreenTimeList(usageStatsList.get(i), appIcon, appName);
            myScreenTimeList.add(screenTimeData);
        }
    }

    //compare the screen time display, the most one will be moved up
    private static class TotalScreenTimeComparator implements Comparator<UsageStats> {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public int compare(UsageStats first, UsageStats second) {
            return Long.compare(second.getTotalTimeInForeground(), first.getTotalTimeInForeground());
        }
    }

    private void CreateBar() {
        ArrayList<BarEntry> apps = new ArrayList<>();
        String[] xData = new String[7];
        if (myScreenTimeList.size() >= 7) {
            for (int i = 0; i < 7; i++) {
                apps.add(new BarEntry(i, myScreenTimeList.get(i).getTotalTimeInForeground()));
                xData[i] = myScreenTimeList.get(i).getAppName();
            }
        } else {
            for (int i = 0; i < myScreenTimeList.size(); i++) {
                apps.add(new BarEntry(i, myScreenTimeList.get(i).getTotalTimeInForeground()));
                //apps.add(new BarEntry(2014,50));
                xData[i] = myScreenTimeList.get(i).getAppName();
            }
        }
        BarDataSet barDataSet = new BarDataSet(apps, "apps");
        int[] colorArray = new int[]{Color.parseColor("#FF0000"),
                Color.parseColor("#FF7F00"),
                Color.parseColor("#FFFF00"),
                Color.parseColor("#00FF00"),
                Color.parseColor("#0000FF"),
                Color.parseColor("#4B0082"),
                Color.parseColor("#9400D3")};
        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setColors(colorArray);
        barDataSet.setDrawValues(false);

        BarData data = new BarData(barDataSet);
        barChart.setData(data);

        XAxis myX = barChart.getXAxis();
        myX.setValueFormatter(new myXAxis(xData));
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setLabelCount(7);
        myX.setGranularity(1f);
        myX.setGranularityEnabled(true);
        myX.setTextColor(Color.parseColor("#FF7F00"));
        //set y axis
        YAxis leftY = barChart.getAxisLeft();
        leftY.setEnabled(false);
        YAxis rightY = barChart.getAxisRight();
        rightY.setDrawLabels(false);
        barChart.setDescription(null);
        barChart.setDrawBorders(true);

        CreateTimeUsed(myScreenTimeList.size(),colorArray);
    }

    private class myXAxis extends ValueFormatter {

        private String[] xValues;

        public myXAxis(String[] xData) {
            this.xValues = xData;
        }


        @Override
        public String getFormattedValue(float value) {
            //return xValues[(int)value];
            return xValues[Math.round(value)];
        }
    }

    private void CreateTimeUsed(int size,int colorArray[]) {
        int appAmount;
        if(size>7){
            appAmount=7;
        }else{
            appAmount=size;
        }
        for(int i=0;i<appAmount;i++){
            View timeUsedView = getLayoutInflater().inflate(R.layout.time_used_layout,null,false);
            ImageView appIcon= (ImageView) timeUsedView.findViewById(R.id.app_icon);
            TextView appName=(TextView) timeUsedView.findViewById(R.id.appName);
            TextView colorUsed=(TextView) timeUsedView.findViewById(R.id.colorUsed);
            TextView usedTime=(TextView) timeUsedView.findViewById(R.id.usedTime);
            TimeUsedDisplay.addView(timeUsedView);
            appIcon.setImageDrawable(myScreenTimeList.get(i).getAppIcon());
            appName.setText(myScreenTimeList.get(i).getAppName());
            colorUsed.setBackgroundColor(colorArray[i]);
            usedTime.setText(myScreenTimeList.get(i).getTime());
        }


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ScreenTimeDisplay.this,MainActivity.class));
    }
}