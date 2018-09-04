package com.foreximf.client.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.foreximf.client.MainActivity;
import com.foreximf.client.R;
import com.foreximf.client.database.ForexImfAppDatabase;
import com.foreximf.client.news.News;
import com.foreximf.client.news.NewsViewModel;
import com.foreximf.client.signal.Signal;
import com.foreximf.client.util.ArchLifecycleApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CloudMessagingService extends FirebaseMessagingService {

    public CloudMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Cloud Messaging Service", "Broadcast Type : " + remoteMessage.getData().get("broadcast_type"));

        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(this.getApplication());
        String dateArray = remoteMessage.getData().get("created_at");
        String dateString = "";
        try {
            dateString = new JSONObject(dateArray).getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String newDateString = "";
        Date date = new Date(System.currentTimeMillis());
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateString);
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd MMM, HH:mm", Locale.ENGLISH);
            sdfOutput.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
            newDateString = sdfOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (remoteMessage.getData().get("broadcast_type")) {
            case "signal" : {
                String type;
                if(remoteMessage.getData().get("type").equals("0")) {
                    type = "News";
                }else {
                    type = "Keterangan";
                }
                Signal signal = new Signal(remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), type, date, 0);
                appDatabase.signalModel().addSignal(signal);
                break;
            }
            case "news" : {
                String type = News.typeConverter(Integer.parseInt(remoteMessage.getData().get("type")));
//                List<News> newsList = new ArrayList<>(appDatabase.newsModel().getNews());
//                newsList.set(Integer.parseInt(remoteMessage.getData().get("type")), new News(type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date));
//                appDatabase.newsModel().deleteAll();
//                appDatabase.newsModel().addNews(newsList.get(0));
//                appDatabase.newsModel().addNews(newsList.get(1));
//                appDatabase.newsModel().addNews(newsList.get(2));
                NewsViewModel newsViewModel = new NewsViewModel(getApplication());
                News newsDbGet = appDatabase.newsModel().getNewsByType(type);
                if(newsDbGet != null) {
                    News newsUpdate = new News(newsDbGet.id, type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), remoteMessage.getData().get("author"), date);
                    newsViewModel.updateNews(newsUpdate);
                }else{
                    News news = new News(type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), remoteMessage.getData().get("author"), date);
                    newsViewModel.addNews(news);
                }
//                News newsUpdate = new News(newsDbGet.id, type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date);
//                News news = new News(type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date);
//                appDatabase.newsModel().deleteNews(newsDbGet);
//                appDatabase.newsModel().addNews(newsUpdate);
//                appDatabase.newsModel().updateNews(remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date, type);
//                }
                break;
            }
            default : {
                break;
            }
        }

//        appDatabase.newsModel().addNews(news);

        //App is in Background
        if(!ArchLifecycleApp.isForeground) {
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification notification = new NotificationCompat.Builder(this, "foreximf-signal")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(newDateString)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup("signal-group")
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setAutoCancel(true)
                    .build();

            int unreadCount = appDatabase.signalModel().getUnreadCount();

            Notification summaryNotification = new NotificationCompat.Builder(this, "foreximf-signal")
                    .setContentTitle("FOREXimf Signal")
                    //set content text to support devices running API level < 24
                    .setContentText("Signals")
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //build summary info into InboxStyle template
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(remoteMessage.getData().get("title"))
                            .setBigContentTitle(String.valueOf(unreadCount - 1)+" more")
                            .setSummaryText("Signals"))
                    //specify which group this notification belongs to
                    .setGroup("signal-group")
                    .setAutoCancel(true)
                    //set this notification as the summary for the group
                    .setGroupSummary(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            createNotificationChannel();
            notificationManager.notify(Integer.parseInt(remoteMessage.getData().get("id")), notification);
            notificationManager.notify(0, summaryNotification);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        final String CHANNEL_ID = "foreximf-signal";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
