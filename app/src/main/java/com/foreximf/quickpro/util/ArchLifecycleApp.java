package com.foreximf.quickpro.util;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.util.Log;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.services.WebSocketChat;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class ArchLifecycleApp extends Application implements LifecycleObserver {

    public static boolean isForeground;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;


    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        EmojiManager.install(new IosEmojiProvider());

        sAnalytics = GoogleAnalytics.getInstance(this);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

    @Override
    public void onTerminate() {
//        unregisterReceiver(smsListener);
        super.onTerminate();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        Log.d("Application", "Berhenti");
        isForeground = false;
        stopService(new Intent(getApplicationContext(), WebSocketChat.class));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        Log.d("Application", "Mulai");
        isForeground = true;
        startService(new Intent(getApplicationContext(), WebSocketChat.class));
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}
