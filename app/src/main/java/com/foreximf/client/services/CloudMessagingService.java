package com.foreximf.client.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.foreximf.client.MainActivity;
import com.foreximf.client.R;
import com.foreximf.client.database.ForexImfAppDatabase;
import com.foreximf.client.news.News;
import com.foreximf.client.news.NewsViewModel;
import com.foreximf.client.signal.Signal;
import com.foreximf.client.signal.SignalRepository;
import com.foreximf.client.util.ArchLifecycleApp;
import com.foreximf.client.util.DateFormatter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class CloudMessagingService extends FirebaseMessagingService {

    public CloudMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("CloudMessagingService", "Broadcast Type : " + remoteMessage.getData().get("broadcast_type"));

        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(this.getApplication());
        Date date = DateFormatter.format(remoteMessage.getData().get("created_at"));
        String newDateString = DateFormatter.format(remoteMessage.getData().get("created_at"), "dd MMM, HH:mm");
        switch (remoteMessage.getData().get("broadcast_type")) {
            case "signal" : {
//                String type;
//                if(remoteMessage.getData().get("type").equals("0")) {
//                    type = "News";
//                }else {
//                    type = "Keterangan";
//                }
                SignalRepository repository = new SignalRepository(getApplication());
                Signal signal = repository.getSignalByServerId(Integer.parseInt(remoteMessage.getData().get("id")));
                if(signal != null) {
                    Log.d("CloudMessagingService", "Signal Server Id : " + signal.getServerId());
                    Log.d("CloudMessagingService", "Signal Title : " + signal.getTitle());
                    Log.d("CloudMessagingService", "Signal Status : " + signal.getStatus());
                    Log.d("CloudMessagingService", "Signal Result : " + remoteMessage.getData().get("result"));
                    signal.setLastUpdate(date);
                    signal.setStatus(Integer.parseInt(remoteMessage.getData().get("status")));
                    signal.setResult(Integer.parseInt(remoteMessage.getData().get("result")));
                }else{
                    signal = new Signal(remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date, 0, Integer.parseInt(remoteMessage.getData().get("currency_pair")), Integer.parseInt(remoteMessage.getData().get("order_type")), Integer.parseInt(remoteMessage.getData().get("result")), Integer.parseInt(remoteMessage.getData().get("status")), Integer.parseInt(remoteMessage.getData().get("signal_group")), Integer.parseInt(remoteMessage.getData().get("id")));
                    Log.d("CloudMessagingService", "Signal Status : " + signal.getStatus());
                }
                if(Integer.parseInt(remoteMessage.getData().get("status")) == 2)
                    repository.addSignal(signal);
                else
                    repository.updateSignal(signal);
                break;
            }
            case "news" : {
                String type = News.typeConverter(Integer.parseInt(remoteMessage.getData().get("type")));
                NewsViewModel newsViewModel = new NewsViewModel(getApplication());
                News newsDbGet = appDatabase.newsModel().getNewsByType(type);
                if(newsDbGet != null) {
                    News newsUpdate = new News(newsDbGet.id, type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), remoteMessage.getData().get("author"), date);
                    newsViewModel.updateNews(newsUpdate);
                }else{
                    News news = new News(type, remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), remoteMessage.getData().get("author"), date);
                    newsViewModel.addNews(news);
                }
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
