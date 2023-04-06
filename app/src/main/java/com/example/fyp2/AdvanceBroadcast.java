package com.example.fyp2;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AdvanceBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskID = intent.getIntExtra("TaskID",0);
        String taskName = intent.getStringExtra("TaskName");
        String DueDate = intent.getStringExtra("DueDate");
        String DueTime = intent.getStringExtra("DueTime");
        String channelID =intent.getStringExtra("channelID");
        Intent mainIntent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder dueNotification=new NotificationCompat.Builder(context,channelID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reminder")
                .setContentText(taskName + " will due on "+DueDate+" "+DueTime+":00")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(context);
        notificationManager.notify(taskID,dueNotification.build());

    }
}
