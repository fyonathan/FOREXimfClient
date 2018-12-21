package com.foreximf.quickpro.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.foreximf.quickpro.chat.ChatDialogRepository;
import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatDepartmentDao;
import com.foreximf.quickpro.database.ForexImfAppDatabase;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketChat extends Service {

    static SharedPreferences sharedPreferences;
    static final String URL = "wss://chat2.foreximf.com:5222/mobile";
    private static WebSocketClient client;
    private static URI uri;
    private static boolean ready;
    private static boolean reconnect;
    private static int timeout_reconnect = 5000;
    private static ScheduledExecutorService scheduledExecutorService;
    private static ChatDialogRepository repository;

    public WebSocketChat() { }

    public static void setUri(URI uri) {
        WebSocketChat.uri = uri;
    }

    public static void setUri(String string) {
        try {
            WebSocketChat.uri = new URI(string);
        } catch (URISyntaxException ex) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("login-token", "");
        WebSocketChat.setUri(URL+"?token="+token);
        repository = new ChatDialogRepository(getApplicationContext());
        if (!token.isEmpty()) {
            this.start();
        }
        return Service.START_STICKY;
    }

    public void start() {
        if (client != null && client.getReadyState() == WebSocket.READYSTATE.OPEN) {
            return;
        }
        ready = false;
        reconnect = true;
        client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("Websocket", "Open");
            }

            @Override
            public void onMessage(String message) {
                Log.d("Websocket", message);
                try {
                    JSONObject data = new JSONObject(message);
                    switch (data.getString("mode")) {
                        case "ready":
                            sharedPreferences.edit().putString("chat-user", data.getString("user")).apply();
                            ready = true;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mode", "sync");
                            WebSocketChat.send(jsonObject);
                            if (scheduledExecutorService != null) {
                                scheduledExecutorService.shutdown();
                            }
                            scheduledExecutorService = Executors.newScheduledThreadPool(1);
                            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("mode", "ping");
                                        WebSocketChat.send(jsonObject);
                                    } catch (Exception ex) {
                                        Log.d("Websocket Error", ex.getMessage());
                                    }
                                }
                            },60, 60, TimeUnit.SECONDS);
                            break;
                        case "departments":
                            JSONArray departments = data.getJSONArray("departments");
                            for(int i = 0; i < departments.length(); i++) {
                                JSONObject obj = departments.getJSONObject(i);
                                ChatDepartment chatDepartment = new ChatDepartment(obj);
                                repository.add(chatDepartment);
                            }
                            break;
                        case "pong":

                            break;
                    }
                } catch (Exception ex) {
                    Log.d("Websocket Error", ex.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("Websocket", "Close: "+code);
                ready = false;
                if (reconnect) {
                    SystemClock.sleep(timeout_reconnect);
                    start();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.d("Websocket", "Error: "+ex.getMessage());
                ready = false;
                client.close();
            }
        };
        client.connect();
    }

    public void stop() {
        ready = false;
        reconnect = false;
        if (client != null) {
            client.close();
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    public static void send(JSONObject jsonObject) {
        Log.d("Websocket", "Send: "+jsonObject.toString());
        if (ready) {
            client.send(jsonObject.toString());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}
