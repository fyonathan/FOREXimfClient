package com.foreximf.quickpro.chat;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatMessage;
import com.foreximf.quickpro.chat.model.ChatUser;
import com.foreximf.quickpro.services.WebSocketChat;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ChatRoomActivity extends Activity {

    ImageLoader imageLoader;
    MessagesList messagesList;
    MessagesListAdapter messagesListAdapter;
    MessageInput messageInput;
    ChatUser chatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.with(ChatRoomActivity.this).load(url).into(imageView);
            }
        };

        messagesList = findViewById(R.id.messages_list);
        messagesListAdapter = new MessagesListAdapter<>("c754bd9ReH0c7cbf79a1f6bec3b3d358099", imageLoader);
        messagesList.setAdapter(messagesListAdapter);

        chatUser = new ChatUser();
        chatUser.setId("c754bd9ReH0c7cbf79a1f6bec3b3d358099");
        chatUser.setNama("Me");

        messageInput = findViewById(R.id.message_input_chat);
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                String id = ""+System.currentTimeMillis();
                ChatMessage message = new ChatMessage();
                message.setId(id);
                message.setMessage(input.toString());
                message.setUser(chatUser);
                message.setTime(new Date());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("thread", "3bfdb5b0Rm907ff6739de06b70a13615f85");
                    jsonObject.put("mode", "message");
                    JSONObject mss = new JSONObject();
                    mss.put("message", input.toString());
                    mss.put("type", "Text");
                    jsonObject.put("message", mss);
                    jsonObject.put("idx", id);
                } catch (JSONException ex) {
                    Log.d("JSON", ex.getMessage());
                }
                WebSocketChat.send(jsonObject);
//                message.save();
//                message.setImage("https://crm.foreximf.com/images/logo.png");
                messagesListAdapter.addToStart(message, true);
                return true;
            }
        });
    }
}
