package com.example.fyp2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int taskID = intent.getIntExtra("TaskID",0);
        String taskName = intent.getStringExtra("TaskName");
        String channelID =intent.getStringExtra("channelID");
        Intent mainIntent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder dueNotification=new NotificationCompat.Builder(context,channelID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reminder")
                .setContentText(taskName+" due on today")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(taskID,dueNotification.build());
    }
}
