package com.foreximf.quickpro.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.foreximf.quickpro.MainActivity;
import com.foreximf.quickpro.R;
import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.foreximf.quickpro.news.News;
import com.foreximf.quickpro.news.NewsViewModel;
import com.foreximf.quickpro.signal.Signal;
import com.foreximf.quickpro.signal.SignalRepository;
import com.foreximf.quickpro.util.ArchLifecycleApp;
import com.foreximf.quickpro.util.DateFormatter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class CloudMessagingService extends FirebaseMessagingService {

    public CloudMessagingService() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Log.d("CloudMessagingService", "New Token : " + s);
        preferences.edit().putString("registration-token", s).apply();
    }

    public static String getToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("registration-token", null);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        Log.d("CloudMessagingService", "Broadcast Type : " + remoteMessage.getData().get("broadcast_type"));

        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(this.getApplication());

//        Log.d("CloudMessagingService", "Empty : " + remoteMessage.getData().isEmpty() + " , " + remoteMessage.getData().get("title"));
        if(remoteMessage.getData().get("broadcast_type") != null) {
            String channelId;
            String title;
            String content;
            String group;
            switch (remoteMessage.getData().get("broadcast_type")) {
                case "signal" : {
//                String type;
//                if(remoteMessage.getData().get("type").equals("0")) {
//                    type = "News";
//                }else {
//                    type = "Keterangan";
//                }
                    JSONObject dS = new JSONObject();
                    try {
                        dS = new JSONObject(remoteMessage.getData().get("created_at"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Date date = null;
                    try {
                        date = DateFormatter.format(dS.getString("date"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String newDateString = DateFormatter.format(remoteMessage.getData().get("created_at"), "dd MMM, HH:mm");
                    channelId = "foreximf-signal";
                    SignalRepository repository = new SignalRepository(getApplication());
                    Signal signal = null;
                    try {
                        signal = repository.getSignalByServerId(Integer.parseInt(remoteMessage.getData().get("id")));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(signal == null) {
                        signal = new Signal(Integer.parseInt(remoteMessage.getData().get("currency_pair")), remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date, Integer.parseInt(remoteMessage.getData().get("order_type")), Integer.parseInt(remoteMessage.getData().get("result")), remoteMessage.getData().get("keterangan"), Integer.parseInt(remoteMessage.getData().get("status")), Integer.parseInt(remoteMessage.getData().get("signal_group")), 0, Integer.parseInt(remoteMessage.getData().get("id")));
//                        Signal signal = new Signal(signalTitle, signalContent, signalDate, signalRead, signalPair, signalOrderType, signalResult, signalStatus, signalGroup, signalId);
                        repository.addSignal(signal);
                    }else{
                        signal = new Signal(signal.getId(), Integer.parseInt(remoteMessage.getData().get("currency_pair")), remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), signal.getCreatedTime(), Integer.parseInt(remoteMessage.getData().get("order_type")), Integer.parseInt(remoteMessage.getData().get("result")), remoteMessage.getData().get("keterangan"), Integer.parseInt(remoteMessage.getData().get("status")), Integer.parseInt(remoteMessage.getData().get("signal_group")), 0, Integer.parseInt(remoteMessage.getData().get("id")));
//                        Signal signal = new Signal(temp.getId(), signalTitle, signalContent, signalDate, signalRead, signalPair, signalOrderType, signalResult, signalStatus, signalGroup, signalId);
                        repository.updateSignal(signal);
                    }

//                    if(signal != null) {
////                        Log.d("CloudMessagingService", "Signal Server Id : " + signal.getServerId());
////                        Log.d("CloudMessagingService", "Signal Title : " + signal.getTitle());
////                        Log.d("CloudMessagingService", "Signal Status : " + signal.getStatus());
////                        Log.d("CloudMessagingService", "Signal Result : " + remoteMessage.getData().get("result"));
//                        signal.setCreatedTime(date);
//                        signal.setStatus(Integer.parseInt(remoteMessage.getData().get("status")));
//                        signal.setResult(Integer.parseInt(remoteMessage.getData().get("result")));
//                    }else{
//                        signal = new Signal(remoteMessage.getData().get("title"), remoteMessage.getData().get("content"), date, 0, Integer.parseInt(remoteMessage.getData().get("currency_pair")), Integer.parseInt(remoteMessage.getData().get("order_type")), Integer.parseInt(remoteMessage.getData().get("result")), Integer.parseInt(remoteMessage.getData().get("status")), Integer.parseInt(remoteMessage.getData().get("signal_group")), Integer.parseInt(remoteMessage.getData().get("id")));
////                        Log.d("CloudMessagingService", "Signal Status : " + signal.getStatus());
//                    }
//                    if(Integer.parseInt(remoteMessage.getData().get("status")) == 2)
//                        repository.addSignal(signal);
//                    else
//                        repository.updateSignal(signal);
                    if(signal.getStatus() == 2 || signal.getStatus() == 3) {
                        title = signal.getTitle();
                        content = newDateString;
                    }else{
                        if(signal.getResult() < 0) {
                            title = "\uD83D\uDCB8 UPDATE "+signal.getCurrencyPairString()+" gagal";
                            content = "Antisipasinya, lihat di sini.";
                        }else{
                            title = "\uD83C\uDFC6 UPDATE "+signal.getCurrencyPairString();
                            content = "Berhasil mencapai target "+signal.getResult();
                        }
                    }
                    group = "signal-group";
                    break;
                }
                case "news" : {
                    channelId = "foreximf-news";
                    Date date = DateFormatter.format(remoteMessage.getData().get("created_at"));
//                    String newDateString = DateFormatter.format(remoteMessage.getData().get("created_at"), "dd MMM, HH:mm");
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
                    title = "";
                    content = "";
                    group = "news-group";
                    break;
                }
                case "camarilla" : {
                    channelId = "foreximf-camarilla";
                    title = remoteMessage.getData().get("title");
                    content = remoteMessage.getData().get("body");
                    group = "camarilla-group";
                    break;
                }
                default : {
                    channelId = "foreximf-general";
                    title = "";
                    content = "";
                    group = "";
                    break;
                }
            }

//        appDatabase.newsModel().addNews(news);

            //App is in Background
            if(!ArchLifecycleApp.isForeground) {
                // Create an Intent for the activity you want to start
                Intent resultIntent = new Intent(this, MainActivity.class);
                if(remoteMessage.getData().get("broadcast_type").equals("camarilla")) {
                    resultIntent.putExtra("fragment", "camarilla");
                }
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Create the TaskStackBuilder and add the intent, which inflates the back stack
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(resultIntent);
                // Get the PendingIntent containing the entire back stack
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Notification notification = new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setGroup(group)
                        .setContentIntent(resultPendingIntent)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .build();

                int unreadCount = appDatabase.signalModel().getUnreadCount();

                Notification summaryNotification = new NotificationCompat.Builder(this, channelId)
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
                        .setGroup(group)
                        .setAutoCancel(true)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                createNotificationChannel("foreximf-signal");
                createNotificationChannel("foreximf-news");
                createNotificationChannel("foreximf-camarilla");
                createNotificationChannel("foreximf-general");

                notificationManager.notify(Integer.parseInt(remoteMessage.getData().get("id")), notification);
                notificationManager.notify(0, summaryNotification);
            }
        }
    }

    private void createNotificationChannel(String channelId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
