package com.foreximf.quickpro.chat;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatMessageComplete;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;

public class ChatMessageInHolder extends MessagesListAdapter.IncomingMessageViewHolder<ChatMessageComplete>{

    public ChatMessageInHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(ChatMessageComplete message) {
        super.onBind(message);
        float scale = itemView.getResources().getDisplayMetrics().density;
        bubble.setPadding((int)(15 * scale + 0.5f), (int)(10 * scale + 0.5f), (int)(15 * scale + 0.5f), (int)(12 * scale + 0.5f));
        time.setTextColor(Color.parseColor("#999999"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        time.setText(simpleDateFormat.format(message.getCreatedAt()));
    }
}
