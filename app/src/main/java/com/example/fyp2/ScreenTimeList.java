package com.example.fyp2;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ScreenTimeList {
    private UsageStats usageStats;
    private Drawable appIcon;
    private long hour;
    private long minute;
    private long second;
    private String time;
    private String appName;
    private long totalTimeInForeground;

    public String getAppName() {
        return appName;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ScreenTimeList(UsageStats usageStats, Drawable appIcon,String appName) {
        this.usageStats = usageStats;
        this.appIcon = appIcon;
        this.totalTimeInForeground = usageStats.getTotalTimeInForeground();
        this.hour = (totalTimeInForeground / (1000 * 60 * 60)) % 24;
        this.minute = (totalTimeInForeground / (1000 * 60)) % 60;
        this.second = (totalTimeInForeground / 1000) % 60;
        this.time = hour+":"+minute+":"+ second;
        this.appName = appName;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public long getHour() {
        return hour;
    }

    public long getMinute() {
        return minute;
    }

    public long getSecond() {
        return second;
    }

    public String getTime() {
        return time;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }
}
