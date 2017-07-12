package com.example.root.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class NotificationAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MyDBHandler dbHandler = new MyDBHandler(context);

        String date;
        String s,task,color;

        NotificationCompat.Builder notification;
        int uniqueID = 1;
        notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);

        notification.setSmallIcon(R.drawable.deadline);
        notification.setTicker("ToDo List");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("DEADLINE is near");

        notification.setDefaults(Notification.DEFAULT_SOUND);

        Intent intent1 = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);


        ArrayList<Tasks> dbString = dbHandler.databaseToString();
        String[] chars,dates;

        date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        for (Tasks curr : dbString) {

            if(curr.get_notify() == 0 ){
                continue;
            }

            s=curr.get_date();
            task=curr.get_taskname();
            color=curr.get_color();
            chars = s.split("/");
            dates = date.split("/");

            if(color.equals("low_priority") && date.equals(s)) {

                notification.setContentText("TASK: "+task);
                //Builds notification and issues it
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(uniqueID, notification.build());
            }
            if(color.equals("medium_priority") && chars[2].equals(dates[2]) && chars[1].equals(dates[1])
                    && Integer.parseInt(chars[0]) - Integer.parseInt(dates[0]) <= 1 ) {

                notification.setContentText("TASK: "+task);
                //Builds notification and issues it
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(uniqueID, notification.build());
            }
            if(color.equals("high_priority") && chars[2].equals(dates[2]) && chars[1].equals(dates[1])
                    && Integer.parseInt(chars[0]) - Integer.parseInt(dates[0]) <= 2 ) {

                notification.setContentText("TASK: "+task);
                //Builds notification and issues it
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(uniqueID, notification.build());
            }
        }

        dbHandler.closeDB();
    }
}
