package com.foreximf.quickpro.chat;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatMessage;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.chat.model.ChatThreadComplete;
import com.foreximf.quickpro.chat.model.ChatUser;
import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.foreximf.quickpro.services.WebSocketChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ChatRepository {
    private static ChatDao chatDao;
    private LiveData<List<ChatThreadComplete>> chatThreadCompleteList;
    private static Context _application;
    private static ChatRepository singleton;

    private ChatRepository(Context application) {
        _application = application;
        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(application);
        chatDao = appDatabase.chatDao();
    }

    public static ChatRepository getInstance(Context context){
        if (singleton == null) {
            singleton = new ChatRepository(context.getApplicationContext());
        }
        return singleton;
    }

    public LiveData<List<ChatThreadComplete>> getChatThreadComplete() {
        if(chatThreadCompleteList == null) {
            chatThreadCompleteList = chatDao.getAllThread();
            if(chatThreadCompleteList == null) {
                chatThreadCompleteList = new MutableLiveData<>();
            }
        }
        return chatThreadCompleteList;
    }

    public List<ChatThread> getActiveThread() {
        return chatDao.getActiveThread();
    }

    public List<ChatThread> getTemporaryThread() { return chatDao.getTemporaryThread(); }

    public void syncLastMessage(String my_id) {
        new getAsyncTask(chatDao).execute(my_id);
    }

    private static class getAsyncTask extends AsyncTask<String, Void, Void> {

        private ChatDao mAsyncTaskDao;
        getAsyncTask(ChatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAsyncTaskDao.syncLastMessage(strings[0]);
            return null;
        }
    }

    public void setSentMessage(String... ids) { new setAsyncTask(chatDao, "sent").execute(ids); }

    public void setReadMessage(String... ids) { new setAsyncTask(chatDao, "read").execute(ids); }

    public void setReadAckMessage(String... ids) { new setAsyncTask(chatDao, "read-ack").execute(ids); }

    public void setIsReadMessage(String id_chat_thread, String... ids) { new setAsyncTask(chatDao, "is_read", id_chat_thread).execute(ids); }

    private static class setAsyncTask extends AsyncTask<String, Void, Void> {

        private String mode;
        private String param;
        private ChatDao mAsyncTaskDao;
        setAsyncTask(ChatDao dao, String mode) {
            mAsyncTaskDao = dao;
            this.mode = mode;
        }
        setAsyncTask(ChatDao dao, String mode, String param) {
            mAsyncTaskDao = dao;
            this.mode = mode;
            this.param = param;
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (mode.equals("read")) {
                mAsyncTaskDao.setReadMessage(1, strings);
            } else if (mode.equals("read-ack")) {
                mAsyncTaskDao.setReadMessage(2, strings);
            } else if (mode.equals("sent")) {
                mAsyncTaskDao.setSentMessage(strings);
            } else if (mode.equals("is_read")) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_application);
                List<ChatMessage> chatMessages = mAsyncTaskDao.getIsReadMessage(sharedPreferences.getString("user-id", ""), strings);
                if (chatMessages.size() > 0) {
                    String[] ids = new String[chatMessages.size()];
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < chatMessages.size(); i++) {
                        ids[i] = chatMessages.get(i).getId();
                        jsonArray.put(ids[i]);
                    }
                    mAsyncTaskDao.setIsReadMessage(ids);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("mode", "read");
                        jsonObject.put("thread", param);
                        jsonObject.put("message", jsonArray);
                        WebSocketChat.send(jsonObject);
                    } catch (JSONException ex) {

                    }
                }
            }
            return null;
        }
    }

    public void add(ChatDepartment chatDepartment) {
        new addChatDepartmentAsyncTask(chatDao).execute(chatDepartment);
    }

    public void add(ChatDepartment... chatDepartments) {
        new addChatDepartmentAsyncTask(chatDao).execute(chatDepartments);
    }

    private static class addChatDepartmentAsyncTask extends AsyncTask<ChatDepartment, Void, Void> {
        private ChatDao mAsyncTaskDao;
        addChatDepartmentAsyncTask(ChatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ChatDepartment... chatDepartments) {
            for(int i = 0; i < chatDepartments.length; i++) {
                mAsyncTaskDao.add(chatDepartments[i]);
            }
            return null;
        }
    }

    public void add(ChatMessage chatMessage) {
        new addChatMessageAsyncTask(chatDao).execute(chatMessage);
    }

    public void add(ChatMessage... chatMessages) {
        new addChatMessageAsyncTask(chatDao).execute(chatMessages);
    }

    public void add(Boolean broadcast, ChatMessage chatMessage) {
        new addChatMessageAsyncTask(chatDao, broadcast).execute(chatMessage);
    }

    public void add(Boolean broadcast, ChatMessage... chatMessages) {
        new addChatMessageAsyncTask(chatDao, broadcast).execute(chatMessages);
    }

    public void add(String old_id, ChatMessage chatMessage) {
        new addChatMessageAsyncTask(chatDao, old_id).execute(chatMessage);
}

    public void add(String old_id, ChatMessage... chatMessages) {
        new addChatMessageAsyncTask(chatDao, old_id).execute(chatMessages);
    }

    private static class addChatMessageAsyncTask extends AsyncTask<ChatMessage, Void, JSONObject> {
        private ChatDao mAsyncTaskDao;
        private Boolean broadcast;
        private String oid;
        addChatMessageAsyncTask(ChatDao dao) {
            mAsyncTaskDao = dao;
            broadcast = false;
        }
        addChatMessageAsyncTask(ChatDao dao, Boolean broadcast) {
            mAsyncTaskDao = dao;
            this.broadcast = broadcast;
        }
        addChatMessageAsyncTask(ChatDao dao, String old_id) {
            mAsyncTaskDao = dao;
            broadcast = true;
            oid = old_id;
        }

        @Override
        protected JSONObject doInBackground(ChatMessage... chatMessages) {
            try {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < chatMessages.length; i++) {
                    mAsyncTaskDao.add(chatMessages[i]);
                    if (broadcast) {
                        if (jsonObject.has(chatMessages[i].getId_chat_thread())) {
                            JSONArray jsonArray = jsonObject.getJSONArray(chatMessages[i].getId_chat_thread());
                            jsonArray.put(chatMessages[i].getId());
                        } else {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(chatMessages[i].getId());
                            jsonObject.put(chatMessages[i].getId_chat_thread(), jsonArray);
                        }
                    }
                }
                return jsonObject;
            } catch (JSONException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (broadcast) {
                try {
                    Iterator<String> temp = jsonObject.keys();
                    while (temp.hasNext()) {
                        String key = temp.next();
                        JSONArray jsonArray = jsonObject.getJSONArray(key);
                        String[] strings = new String[jsonArray.length()];
                        for(int i = 0; i < jsonArray.length(); i++) {
                            strings[i] = jsonArray.getString(i);
                        }
                        Intent intent = new Intent("chat_message");
                        intent.putExtra("chat", key);
                        intent.putExtra("ids", strings);
                        if (oid != null && !oid.isEmpty()) {
                            intent.putExtra("old_id", oid);
                        }
                        LocalBroadcastManager.getInstance(_application).sendBroadcast(intent);
                    }
                } catch (Exception ex) {

                }
            }
        }
    }

    public void add(ChatThread chatThread) {
        new addChatThreadAsyncTask(chatDao).execute(chatThread);
    }

    public void add(ChatThread chatThread, Callback callback) {
        new addChatThreadAsyncTask(chatDao, callback).execute(chatThread);
    }

    public void add(ChatThread... chatThreads) {
        new addChatThreadAsyncTask(chatDao).execute(chatThreads);
    }

    private static class addChatThreadAsyncTask extends AsyncTask<ChatThread, Void, Void> {
        private ChatDao mAsyncTaskDao;
        private Callback callback;

        addChatThreadAsyncTask(ChatDao dao) {
            mAsyncTaskDao = dao;
        }

        addChatThreadAsyncTask(ChatDao dao, Callback callback) {
            mAsyncTaskDao = dao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ChatThread... chatThreads) {
            for(int i = 0; i < chatThreads.length; i++) {
                mAsyncTaskDao.add(chatThreads[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.after();
            }
        }
    }

    public interface Callback {
        void after();
    }

    public void add(ChatUser chatUser) {
        new addChatUserAsyncTask(chatDao).execute(chatUser);
    }

    public void add(ChatUser... chatUsers) {
        new addChatUserAsyncTask(chatDao).execute(chatUsers);
    }

    private static class addChatUserAsyncTask extends AsyncTask<ChatUser, Void, Void> {
        private ChatDao mAsyncTaskDao;
        addChatUserAsyncTask(ChatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ChatUser... chatUsers) {
            for(int i = 0; i < chatUsers.length; i++) {
                mAsyncTaskDao.add(chatUsers[i]);
            }
            return null;
        }
    }

    public void update(ChatThread chatThread) {
        new updateAsyncTask(chatDao).execute(chatThread);
    }

    public void update(ChatThread... chatThreads) {
        new updateAsyncTask(chatDao).execute(chatThreads);
    }

    private static class updateAsyncTask extends AsyncTask<ChatThread, Void, Void> {
        private ChatDao mAsyncTaskDao;
        updateAsyncTask(ChatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ChatThread... chatThreads) {
            for(int i = 0; i < chatThreads.length; i++) {
                mAsyncTaskDao.update(chatThreads[i]);
            }
            return null;
        }
    }

    public void deleteChatMessage(String string) {
        new deleteChatMessageAsyncTask(chatDao).execute(string);
    }

    public void deleteChatMessage(String... strings) {
        new deleteChatMessageAsyncTask(chatDao).execute(strings);
    }

    private static class deleteChatMessageAsyncTask extends AsyncTask<String, Void, Void> {
        private ChatDao mAsyncTaskDao;
        deleteChatMessageAsyncTask(ChatDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(String... strings) {
            for(int i = 0; i < strings.length; i++) {
                ChatMessage chatMessage = mAsyncTaskDao.findMessage(strings[i]);
                if (chatMessage != null) {
                    mAsyncTaskDao.delete(chatMessage);
                }
            }
            return null;
        }
    }

    public void setUserStatus(String... strings) {
        new setUserStatusAsyncTask(chatDao, this).execute(strings);
    }

    private static class setUserStatusAsyncTask extends AsyncTask<String, Void, Void> {
        private ChatDao mAsyncTaskDao;
        private ChatRepository chatRepository;
        setUserStatusAsyncTask(ChatDao dao, ChatRepository chatRepository) { mAsyncTaskDao = dao; this.chatRepository = chatRepository; }

        @Override
        protected Void doInBackground(String... strings) {
            ChatUser chatUser = mAsyncTaskDao.findUser(strings[0]);
            if (chatUser == null && !strings[1].equals("none")) {
                WebSocketChat.requestUser(strings[0]);
            } else {
                switch (strings[1]) {
                    case "none": {
                        String message = "";
                        Intent intent = new Intent("chat_message");
                        intent.putExtra("chat", strings[2]);
                        intent.putExtra("status", message);
                        LocalBroadcastManager.getInstance(_application).sendBroadcast(intent);
                        break;
                    }
                    case "typing": {
                        String message = chatUser.getName()+" is typing...";
                        Intent intent = new Intent("chat_message");
                        intent.putExtra("chat", strings[2]);
                        intent.putExtra("status", message);
                        LocalBroadcastManager.getInstance(_application).sendBroadcast(intent);
                        break;
                    }
                    case "online": {
                        chatUser.setStatus("Online");
                        chatUser.setLast_online(new Date());
                        chatRepository.add(chatUser);
                        String message = chatUser.getName()+" is online";
                        Intent intent = new Intent("chat_message");
                        intent.putExtra("chat", strings[2]);
                        intent.putExtra("status", message);
                        LocalBroadcastManager.getInstance(_application).sendBroadcast(intent);
                        break;
                    }
                    case "offline": {
                        try {
                            chatUser.setStatus("Offline");
                            Date date = new Date(Long.parseLong(strings[3]));
                            chatUser.setLast_online(date);
                            chatRepository.add(chatUser);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY HH:mm");
                            String message = chatUser.getName()+" is offline. Last seen "+simpleDateFormat.format(date);
                            Intent intent = new Intent("chat_message");
                            intent.putExtra("chat", strings[2]);
                            intent.putExtra("status", message);
                            LocalBroadcastManager.getInstance(_application).sendBroadcast(intent);
                        } catch (Exception ex) {

                        }

                        break;
                    }
                }
            }
            return null;
        }
    }
}
