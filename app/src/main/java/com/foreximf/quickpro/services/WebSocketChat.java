package com.foreximf.quickpro.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.foreximf.quickpro.chat.ChatRepository;
import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatMessage;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.chat.model.ChatUser;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
    private static ChatRepository repository;

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
        repository = ChatRepository.getInstance(getApplicationContext());
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
                        case "ready": {
                            sharedPreferences.edit().putString("chat-user", data.getString("user")).apply();
                            ready = true;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mode", "sync");
                            JSONArray jsonArray = new JSONArray();
                            List<ChatThread> chatThreads = repository.getActiveThread();
                            for (int i = 0; i < chatThreads.size(); i++) {
                                JSONObject thread = new JSONObject();
                                thread.put("thread", chatThreads.get(i).getId());
                                thread.put("last_message", chatThreads.get(i).getId_last_message());
                                jsonArray.put(thread);
                            }
                            jsonObject.put("threads", jsonArray);
                            WebSocketChat.send(jsonObject);

                            List<ChatThread> temps = repository.getTemporaryThread();
                            if (temps.size() > 0) {
                                JSONObject obj = new JSONObject();
                                JSONArray array = new JSONArray();
                                obj.put("mode", "temporary-thread");
                                for (int i = 0; i < temps.size(); i++) {
                                    JSONObject object = new JSONObject();
                                    object.put("id", temps.get(i).getId());
                                    object.put("department", temps.get(i).getId_chat_department());
                                    array.put(object);
                                }
                                obj.put("threads", array);
                                WebSocketChat.send(obj);
                            }
                            if (scheduledExecutorService != null) {
                                scheduledExecutorService.shutdown();
                            }
                            scheduledExecutorService = Executors.newScheduledThreadPool(1);
                            scheduledExecutorService.scheduleAtFixedRate(() -> {
                                try {
                                    JSONObject jsonObject1 = new JSONObject();
                                    jsonObject1.put("mode", "ping");
                                    WebSocketChat.send(jsonObject1);
                                } catch (Exception ex) {
                                    Log.d("Websocket Error", message);
                                }
                            }, 60, 60, TimeUnit.SECONDS);
                            break;
                        }
                        case "ack": {
                            repository.deleteChatMessage(data.getString("idx"));
                            repository.add(data.getString("idx"), new ChatMessage(data.getJSONObject("message")));
                            break;
                        }
                        case "departments": {
                            JSONArray departments = data.getJSONArray("departments");
                            for (int i = 0; i < departments.length(); i++) {
                                repository.add(new ChatDepartment(departments.getJSONObject(i)));
                            }
                            break;
                        }
                        case "message": {
                            ChatMessage chatMessage = new ChatMessage(data.getJSONObject("message"));
                            repository.add(true, chatMessage);
                            repository.syncLastMessage(sharedPreferences.getString("user-id", ""));
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mode", "ack");
                            jsonObject.put("thread", data.getString("chat"));
                            jsonObject.put("message", chatMessage.getId());
                            WebSocketChat.send(jsonObject);
                            break;
                        }
                        case "messages": {
                            JSONArray messages = data.getJSONArray("messages");
                            JSONArray ids = new JSONArray();
                            ChatMessage[] chatMessages = new ChatMessage[messages.length()];
                            for (int i = 0; i < messages.length(); i++) {
                                chatMessages[i] = new ChatMessage(messages.getJSONObject(i));
                                ids.put(chatMessages[i].getId());
                            }
                            repository.add(true, chatMessages);
                            repository.syncLastMessage(sharedPreferences.getString("user-id", ""));
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mode", "ack");
                            jsonObject.put("thread", data.getString("chat"));
                            jsonObject.put("message", ids);
                            WebSocketChat.send(jsonObject);
                            break;
                        }
                        case "read": {
                            JSONArray messages = data.getJSONArray("messages");
                            String[] ids = new String[messages.length()];
                            for(int i = 0; i < messages.length(); i++) {
                                ids[i] = messages.getString(i);
                            }
                            repository.setReadMessage(ids);
                            break;
                        }
                        case "read-ack": {
                            JSONArray messages = data.getJSONArray("messages");
                            String[] ids = new String[messages.length()];
                            for(int i = 0; i < messages.length(); i++) {
                                ids[i] = messages.getString(i);
                            }
                            repository.setReadAckMessage(ids);
                            break;
                        }
                        case "sent": {
                            JSONArray messages = data.getJSONArray("messages");
                            String[] ids = new String[messages.length()];
                            for(int i = 0; i < messages.length(); i++) {
                                ids[i] = messages.getString(i);
                            }
                            repository.setSentMessage(ids);
                            break;
                        }
                        case "status": {
                            repository.setUserStatus(data.optString("user"), data.optString("status"), data.optString("chat"), data.optString("last_online"));
                            break;
                        }
                        case "thread": {
                            repository.add(new ChatThread(data.getJSONObject("thread")));
                            repository.syncLastMessage(sharedPreferences.getString("user-id", ""));
                            break;
                        }
                        case "threads": {
                            JSONArray threads = data.getJSONArray("threads");
                            for (int i = 0; i < threads.length(); i++) {
                                repository.add(new ChatThread(threads.getJSONObject(i)));
                            }
                            repository.syncLastMessage(sharedPreferences.getString("user-id", ""));
                            break;
                        }
                        case "user": {
                            repository.add(new ChatUser(data.getJSONObject("user")));
                            break;
                        }
                        case "users": {
                            JSONArray users = data.getJSONArray("users");
                            for (int i = 0; i < users.length(); i++) {
                                repository.add(new ChatUser(users.getJSONObject(i).getJSONObject("user")));
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    String[] mss = ex.getMessage().split("\n");
                    for(int i = 0; i < mss.length; i++) {
                        Log.d("Websocket Error", mss[i]);
                    }
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

    public static void requestUser(String id) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("mode", "user");
            obj.put("user", id);
        } catch (Exception ex) { }
        send(obj);
    }

    public static ChatRepository getRepository() {
        return repository;
    }
}
