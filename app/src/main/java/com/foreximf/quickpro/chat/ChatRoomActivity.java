package com.foreximf.quickpro.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatMessage;
import com.foreximf.quickpro.chat.model.ChatMessageComplete;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.chat.model.ChatUser;
import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.foreximf.quickpro.services.WebSocketChat;
import com.foreximf.quickpro.util.F;
import com.foreximf.quickpro.util.ImageUtils;
import com.foreximf.quickpro.util.TakePhotoActivity;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomActivity extends AppCompatActivity implements
        MessagesListAdapter.OnMessageLongClickListener<ChatMessageComplete>,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener,
        MessageHolders.ContentChecker<ChatMessageComplete> {

    private static final int GET_FROM_GALLERY = 3;
    private static final byte CONTENT_TYPE_IMAGE = 1;

    SharedPreferences sharedPreferences;
    ImageLoader imageLoader;

    Toolbar toolbar;
    ImageButton back;
    CircleImageView avatar;
    TextView name;
    TextView status;

    MessagesList messagesList;
    MessagesListAdapter messagesListAdapter;
    MessageInput messageInput;
    String id_chat_thread;
    ChatThread chatThread;
    String my_id;
    ChatUser me;
    Boolean isTyping;

    ChatRepository chatRepository;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("chat") && intent.getStringExtra("chat").equals(id_chat_thread)) {
                if (intent.hasExtra("status")) {
                    status.setText(intent.getStringExtra("status"));
                } else if (intent.hasExtra("old_id")) {
                    String[] messages = intent.getStringArrayExtra("ids");
                    new ChatMessageAsyncTask(ChatRoomActivity.this, intent.getStringExtra("old_id")).execute(messages);
                } else {
                    String[] messages = intent.getStringArrayExtra("ids");
                    new ChatMessageAsyncTask(ChatRoomActivity.this).execute(messages);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(ChatRoomActivity.this, R.color.colorLightBlue));
        }

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        my_id = sharedPreferences.getString("user-id", "");
        Intent i = getIntent();
        id_chat_thread = i.getStringExtra("ChatThread");
        imageLoader = (imageView, url, payload) -> ImageUtils.loadImagePicasso(ChatRoomActivity.this, url, imageView);
        chatRepository = WebSocketChat.getRepository();
        if (chatRepository == null) {
            chatRepository = ChatRepository.getInstance(getApplicationContext());
        }

        new InitAsyncTask(this).execute();

        back = toolbar.findViewById(R.id.back);
        back.setOnClickListener(view -> {
            onBackPressed();
        });
        avatar = toolbar.findViewById(R.id.avatar);
        name = toolbar.findViewById(R.id.name);
        status = toolbar.findViewById(R.id.status);

        messagesList = findViewById(R.id.messages_list);
        MessageHolders holders = new MessageHolders();
        holders.setIncomingTextLayout(R.layout.incoming_text_message);
        holders.setIncomingTextHolder(ChatMessageInHolder.class);
        holders.setOutcomingTextLayout(R.layout.outcoming_text_message);
        holders.setOutcomingTextHolder(ChatMessageOutHolder.class);

        holders.registerContentType(CONTENT_TYPE_IMAGE, IncomingImageViewHolder.class, R.layout.incoming_image_message, OutcomingImageViewHolder.class, R.layout.outcoming_image_message, this);
        messagesListAdapter = new MessagesListAdapter<>(my_id, holders, imageLoader);
        messagesList.setAdapter(messagesListAdapter);
        messagesListAdapter.setOnMessageLongClickListener(this);

        messageInput = findViewById(R.id.message_input_chat);
        messageInput.setInputListener(input -> {
            String id = ""+System.currentTimeMillis();
            ChatMessageComplete chatMessageComplete = new ChatMessageComplete();
            ChatMessage message = new ChatMessage();
            message.setId(id);
            message.setId_chat_thread(id_chat_thread);
            message.setId_chat_user(me.getId());
            message.setType("Temporary:Text");
            message.setMessage(input.toString());
            message.setIs_read(0);
            message.setSent_count(0);
            message.setRead_count(0);
            message.setTime(new Date());
            chatMessageComplete.chatMessage = message;
            chatMessageComplete.chatUser = new ArrayList<>();
            chatMessageComplete.chatUser.add(me);
            chatRepository.add(message);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mode", "message");
                JSONObject mss = new JSONObject();
                mss.put("message", input.toString());
                mss.put("type", "Text");
                jsonObject.put("thread", id_chat_thread);
                jsonObject.put("message", mss);
                jsonObject.put("idx", id);
            } catch (JSONException ex) {
                Log.d("JSON", ex.getMessage());
            }
            WebSocketChat.send(jsonObject);
            messagesListAdapter.addToStart(chatMessageComplete, true);
            return true;
        });
        isTyping = false;
        messageInput.setTypingListener(this);
        messageInput.setAttachmentsListener(this);

        messagesListAdapter.setLoadMoreListener((page, totalItemsCount) -> new LoadMoreAsyncTask(ChatRoomActivity.this).execute(page));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chat_message"));
    }

    @Override
    public boolean hasContentFor(ChatMessageComplete message, byte type) {
        switch (type) {
            case CONTENT_TYPE_IMAGE: {
                return message.chatMessage.getType().equals("Image") || message.chatMessage.getType().equals("Temporary:Image");
            }
        }
        return false;
    }

    @Override
    public void onStartTyping() {
        if (!isTyping) {
            isTyping = true;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("mode", "typing");
                jsonObject.put("thread", id_chat_thread);
                jsonObject.put("typing", 1);
                WebSocketChat.send(jsonObject);
            } catch (JSONException ex) {
                Log.d("JSON", ex.getMessage());
            }
        }
    }

    @Override
    public void onStopTyping() {
        if (isTyping) {
            isTyping = false;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("mode", "typing");
                jsonObject.put("thread", id_chat_thread);
                jsonObject.put("typing", 0);
                WebSocketChat.send(jsonObject);
            } catch (JSONException ex) {
                Log.d("JSON", ex.getMessage());
            }
        }
    }

    @Override
    public void onAddAttachments() {
        Log.d("Attach", "Clicked");
        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItem(new PowerMenuItem("Camera", false))
                .addItem(new PowerMenuItem("Select Photo", false))
                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                .setDividerHeight(1)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setLifecycleOwner(this)
                .build();
        powerMenu.setOnMenuItemClickListener((position, item) -> {
            switch (item.getTitle()) {
                case "Camera": {
                    powerMenu.dismiss();
                    startActivityForResult(new Intent(ChatRoomActivity.this, TakePhotoActivity.class), TakePhotoActivity.TAKE_PHOTO);
                    break;
                }
                case "Select Photo": {
                    powerMenu.dismiss();
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                    break;
                }
            }
        });
        powerMenu.showAtCenter(messageInput);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GET_FROM_GALLERY || requestCode == TakePhotoActivity.TAKE_PHOTO) && resultCode == Activity.RESULT_OK) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChatRoomActivity.this);
            ProgressDialog dialog = new ProgressDialog(ChatRoomActivity.this);
            dialog.setMessage("Uploading...");
            dialog.show();
            String url = "https://client.foreximf.com/api-chat";
            Map<String, String> params = new HashMap<>();
            params.put("token", preferences.getString("login-token", ""));
            params.put("mode", "image");
            Bitmap bmp = null;
            if (requestCode == GET_FROM_GALLERY) {
                Uri selectedImage = data.getData();
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TakePhotoActivity.TAKE_PHOTO) {
                if (data.hasExtra("uri")) {
                    String filePath = data.getStringExtra("uri");
                    File file = new File(filePath);
                    bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    file.deleteOnExit();
                }
            }

            if (bmp != null) {
                int limit_size = 960;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap;
                if (bmp.getWidth() > limit_size && bmp.getWidth() >= bmp.getHeight()) {
                    bitmap = Bitmap.createScaledBitmap(bmp, limit_size, (int) Math.floor(limit_size * bmp.getHeight() / bmp.getWidth()), false);
                } else if (bmp.getHeight() > limit_size && bmp.getHeight() > bmp.getWidth()) {
                    bitmap = Bitmap.createScaledBitmap(bmp, (int) Math.floor(limit_size * bmp.getWidth() / bmp.getHeight()), limit_size, false);
                } else {
                    bitmap = bmp;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                params.put("content", Base64.encodeToString(imageBytes, Base64.DEFAULT));

                F.JSONRequest(this, url, params, new F.JSONRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        dialog.dismiss();
                        try {
                            boolean error = response.getBoolean("error");
                            if (!error) {
                                LayoutInflater li = LayoutInflater.from(ChatRoomActivity.this);
                                View view = li.inflate(R.layout.dialog_send_chat_image, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatRoomActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
                                alertDialogBuilder.setView(view);
                                ImageView imageView = view.findViewById(R.id.image);
                                EditText editText = view.findViewById(R.id.caption);
                                ImageUtils.loadImagePicasso(ChatRoomActivity.this, response.getString("url"), imageView);

                                alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("Send", (dialog12, id) -> {
                                        String idx = ""+System.currentTimeMillis();
                                        JSONObject jsonMessage = new JSONObject();
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonMessage.put("image", response.getString("path"));
                                            jsonMessage.put("label", editText.getText().length() == 0 ? null : editText.getText());
                                            jsonObject.put("mode", "message");
                                            JSONObject mss = new JSONObject();
                                            mss.put("message", jsonMessage.toString());
                                            mss.put("type", "Image");
                                            jsonObject.put("thread", id_chat_thread);
                                            jsonObject.put("message", mss);
                                            jsonObject.put("idx", idx);
                                        } catch (JSONException ex) {
                                            Log.d("JSON", ex.getMessage());
                                        }
                                        ChatMessageComplete chatMessageComplete = new ChatMessageComplete();
                                        ChatMessage message = new ChatMessage();
                                        message.setId(idx);
                                        message.setId_chat_thread(id_chat_thread);
                                        message.setId_chat_user(me.getId());
                                        message.setType("Temporary:Image");
                                        message.setMessage(jsonMessage.toString());
                                        message.setIs_read(0);
                                        message.setSent_count(0);
                                        message.setRead_count(0);
                                        message.setTime(new Date());
                                        chatMessageComplete.chatMessage = message;
                                        chatMessageComplete.chatUser = new ArrayList<>();
                                        chatMessageComplete.chatUser.add(me);
                                        chatRepository.add(message);
                                        WebSocketChat.send(jsonObject);
                                        messagesListAdapter.addToStart(chatMessageComplete, true);
                                    })
                                    .setNegativeButton("Cancel", (dialog1, id) -> dialog1.cancel());
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                alertDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(ChatRoomActivity.this, "Muncul error saat mencoba upload gambar, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void onMessageLongClick(ChatMessageComplete message) {
        Log.d("Long", message.getId() + "|" + message.chatMessage.getType() + "|" + message.getText());
    }

    private class InitAsyncTask extends AsyncTask<Void, Void, List<ChatMessageComplete>> {
        private ProgressDialog dialog;
        private ChatDao chatDao;

        public InitAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
            chatDao = ForexImfAppDatabase.getDatabase(activity).chatDao();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected List<ChatMessageComplete> doInBackground(Void... args) {
            chatThread = chatDao.findThread(id_chat_thread);
            me = chatDao.findUser(my_id);
            if (me == null) {
                me = new ChatUser();
                me.setId(my_id);
                me.setAvatar(sharedPreferences.getString("user-avatar", ""));
                me.setName(sharedPreferences.getString("user-name", ""));
                WebSocketChat.requestUser(my_id);
            }
            return chatDao.getMessagesByThread(id_chat_thread, 20, 0);
        }

        @Override
        protected void onPostExecute(List<ChatMessageComplete> result) {
            ImageUtils.loadImagePicasso(ChatRoomActivity.this, chatThread.getAvatar(), avatar);
            name.setText(chatThread.getName());
            if (result != null && !result.isEmpty()) {
                messagesListAdapter.addToEnd(result, false);
                String[] ids = new String[result.size()];
                for(int i = 0; i < result.size(); i++) {
                    ids[i] = result.get(i).getId();
                }
                chatRepository.setIsReadMessage(id_chat_thread, ids);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class ChatMessageAsyncTask extends AsyncTask<String, Void, List<ChatMessageComplete>> {
        private ChatDao chatDao;
        private String oid;

        public ChatMessageAsyncTask(Activity activity) {
            chatDao = ForexImfAppDatabase.getDatabase(activity).chatDao();
        }

        public ChatMessageAsyncTask(Activity activity, String old_id) {
            chatDao = ForexImfAppDatabase.getDatabase(activity).chatDao();
            oid = old_id;
        }

        @Override
        protected List<ChatMessageComplete> doInBackground(String... strings) {
            List<ChatMessageComplete> result = new ArrayList<>();
            for(int i = 0; i < strings.length; i++) {
                result.add(chatDao.getMessage(strings[i]));
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<ChatMessageComplete> result) {
            if (oid != null && !oid.isEmpty()) {
                messagesListAdapter.update(oid, result.get(0));
            } else {
                String[] ids = new String[result.size()];
                for(int i = 0; i < result.size(); i++) {
                    messagesListAdapter.upsert(result.get(i));
                    ids[i] = result.get(i).getId();
                }
                chatRepository.setIsReadMessage(id_chat_thread, ids);
            }
            messagesList.scrollToPosition(0);
        }
    }

    private class LoadMoreAsyncTask extends AsyncTask<Integer, Void, List<ChatMessageComplete>> {
        private ChatDao chatDao;

        public LoadMoreAsyncTask(Activity activity) {
            chatDao = ForexImfAppDatabase.getDatabase(activity).chatDao();
        }

        @Override
        protected List<ChatMessageComplete> doInBackground(Integer... args) {
            return chatDao.getMessagesByThread(id_chat_thread, 5, args[0]);
        }

        @Override
        protected void onPostExecute(List<ChatMessageComplete> result) {
            if (result != null && !result.isEmpty()) {
                messagesListAdapter.addToEnd(result, false);
                String[] ids = new String[result.size()];
                for(int i = 0; i < result.size(); i++) {
                    ids[i] = result.get(i).getId();
                }
                chatRepository.setIsReadMessage(id_chat_thread, ids);
            }
        }
    }
}
