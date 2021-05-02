package com.example.a22056_app.Tools;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.a22056_app.Activities.PatientListActivity;
import com.example.a22056_app.R;

public class Notification extends AppCompatActivity {

    private static final String CHANNEL_ID = "myChannelid";
    private static final String CHANNEL_NAME = "myChannelName";
    private NotificationManagerCompat mNotificationManagerCompat;
    public static final int NOTIFICATION_ID = 888;


    public void addNotification(Context context, String nameText){
        Log.i("DEBUGNOTIFY", "addNotification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        //builder.setCategory("alarm");
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentTitle("*Alert* " + nameText);
        builder.setContentText("In high risk");
        builder.setAutoCancel(true);

        Intent intent = new Intent(context, PatientListActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,Intent.FILL_IN_CATEGORIES);

        builder.setContentIntent(pi);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel((channel));
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1,builder.build());


    }


    private void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME; // context.getString(R.string.channel_1_name)
            String description = CHANNEL_NAME; // context.getString(R.string.channel_1_description)

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription(description);
            channel.setName(name);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
